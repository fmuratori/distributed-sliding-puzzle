package part2test

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.ddata.typed.scaladsl.{DistributedData, Replicator}
import akka.cluster.ddata.{ORMultiMap, ORMultiMapKey, SelfUniqueAddress}

object DataActorListener {
  sealed trait Command
  case class ViewReady(puzzleBoard: PuzzleBoard) extends Command
  case class Increment(currentMap: List[Int]) extends Command
  final case class GetValue(replyTo: ActorRef[List[Int]]) extends Command
  final case class GetCachedValue(replyTo: ActorRef[List[Int]]) extends Command
  case object Unsubscribe extends Command
  private sealed trait InternalCommand extends Command
  private case class InternalUpdateResponse(rsp: Replicator.UpdateResponse[ORMultiMap[String, List[Int]]]) extends InternalCommand
  private case class InternalGetResponse(rsp: Replicator.GetResponse[ORMultiMap[String, List[Int]]],
                                         replyTo: ActorRef[List[Int]])
    extends InternalCommand
  private case class InternalSubscribeResponse(chg: Replicator.SubscribeResponse[ORMultiMap[String, List[Int]]]) extends InternalCommand

  private var puzzleBoard:PuzzleBoard = null

  def apply(): Behavior[Command] =
    Behaviors.setup[Command] { context =>
      implicit val node: SelfUniqueAddress = DistributedData(context.system).selfUniqueAddress

      // adapter that turns the response messages from the replicator into our own protocol
      DistributedData.withReplicatorMessageAdapter[Command, ORMultiMap[String, List[Int]]] { replicatorAdapter =>
        // Subscribe to changes of the given `key`.
        val key:ORMultiMapKey[String, List[Int]] = ORMultiMapKey("map")

        replicatorAdapter.subscribe(key, InternalSubscribeResponse.apply)

        def updated(cachedValue: List[Int]): Behavior[Command] = Behaviors.receiveMessage[Command] {
          case ViewReady(board) =>
            this.puzzleBoard = board
            Behaviors.same

          case Increment(map) =>
            replicatorAdapter.askUpdate(
              askReplyTo => Replicator.Update(key, ORMultiMap.empty[String, List[Int]], Replicator.WriteLocal, askReplyTo)
              (x => x.put(node, "map", Set(map))),
              InternalUpdateResponse.apply)

            Behaviors.same

          case GetValue(replyTo) =>
            replicatorAdapter.askGet(
              askReplyTo => Replicator.Get(key, Replicator.ReadLocal, askReplyTo),
              value => InternalGetResponse(value, replyTo))

            Behaviors.same

          case GetCachedValue(replyTo) =>
            replyTo ! cachedValue
            Behaviors.same

          case Unsubscribe =>
            replicatorAdapter.unsubscribe(key)
            Behaviors.same

          case internal: InternalCommand =>
            internal match {
              case InternalUpdateResponse(rsp) => {
                /* l'attore locale cerca di effettuare un update */
                Behaviors.same
              } // ok

              case InternalGetResponse(rsp @ Replicator.GetSuccess(`key`), replyTo) =>
                val value: List[Int] = rsp.get(key).get("map").get.toList.head
                replyTo ! value
                Behaviors.same

              case InternalGetResponse(_, _) =>
                Behaviors.unhandled // not dealing with failures

              case InternalSubscribeResponse(chg @ Replicator.Changed(`key`)) =>
                /* il cluster divulga il nuovo valore a tutti gli attori */
                val value = chg.get(key).get("map").get.toList.head
                println(value)
                puzzleBoard.updateBoard(value)
                updated(value)

              case InternalSubscribeResponse(Replicator.Deleted(_)) =>
                Behaviors.unhandled // no deletes

              case InternalSubscribeResponse(_) => // changed but wrong key
                Behaviors.unhandled

            }
        }

        updated(cachedValue = List(1, 2, 3, 4))
      }
    }
}