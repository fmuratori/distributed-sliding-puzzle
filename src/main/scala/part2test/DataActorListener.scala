package part2test

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.ddata.typed.scaladsl.{DistributedData, Replicator}
import akka.cluster.ddata.{Flag, FlagKey, ORMultiMap, ORMultiMapKey, SelfUniqueAddress}
import part2.DataActorListener.InternalUpdateResponse

object DataActorListener {
  sealed trait Command
  case class ViewReady(puzzleBoard: PuzzleBoard) extends Command
  case class Increment(currentMap: List[Int]) extends Command
  final case class GetValue() extends Command
  private sealed trait InternalCommand extends Command
  private case class InternalUpdateResponse(rsp: Replicator.UpdateResponse[ORMultiMap[String, List[Int]]]) extends InternalCommand
  private case class InternalGetResponse(rsp: Replicator.GetResponse[ORMultiMap[String, List[Int]]])
    extends InternalCommand
  private case class InternalSubscribeResponse(chg: Replicator.SubscribeResponse[ORMultiMap[String, List[Int]]]) extends InternalCommand

  private var puzzleBoard:Option[PuzzleBoard] = None

  def apply(): Behavior[Command] =
    Behaviors.setup[Command] { context =>
      implicit val node: SelfUniqueAddress = DistributedData(context.system).selfUniqueAddress

      // adapter that turns the response messages from the replicator into our own protocol
      DistributedData.withReplicatorMessageAdapter[Command, ORMultiMap[String, List[Int]]] {
        replicatorAdapter => {

          // Subscribe to changes of the given `key`.
          val key: ORMultiMapKey[String, List[Int]] = ORMultiMapKey("map")

          replicatorAdapter.subscribe(key, InternalSubscribeResponse.apply)

          def updated(): Behavior[Command] = Behaviors.receiveMessage[Command] {
            case ViewReady(board) =>
              this.puzzleBoard = Option(board)
              Behaviors.same

            case Increment(map) =>
              replicatorAdapter.askUpdate(
                askReplyTo => Replicator.Update(key, ORMultiMap.empty[String, List[Int]], Replicator.WriteLocal, askReplyTo)
                (x => x.put(node, "map", Set(map))),
                InternalUpdateResponse.apply)
                // println("Increment request: ", map)
              Behaviors.same

            case GetValue() =>
              replicatorAdapter.askGet(
                askReplyTo => Replicator.Get(key, Replicator.ReadLocal, askReplyTo),
                value => InternalGetResponse(value))

              Behaviors.same

            case internal: InternalCommand =>
              internal match {
                case InternalUpdateResponse(rsp) => {
                  Behaviors.same
                }

                case InternalGetResponse(value) =>
                  println(value)
                  Behaviors.unhandled

                case InternalSubscribeResponse(chg@Replicator.Changed(`key`)) =>
                  /* il cluster divulga il nuovo valore a tutti gli attori */
                  val value = chg.get(key).get("map").get.toList.head
                  println("Increment response: ", value)
                  puzzleBoard.get.updateBoard(value)
                  updated()
              }
          }
          updated()
        }
      }
    }
}