package part2.akka

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.ddata.typed.scaladsl.{DistributedData, Replicator}
import akka.cluster.ddata.{LWWMap, LWWMapKey, SelfUniqueAddress}

object DistributedDataActor {

  /*
  * E' usato un LWWMap per garantire la consistenza della partita in caso di mosse differenti eseguite da molteplici
  * utenti contemporaneamente.
  *
  * LWW = l'ultima operazione ricevuta e da eseguire (si assume che le azioni siano mantenute in una coda) vince su
  * tutte le altre.
  *
  * Una struttura come la ORMultiMap non garantisce questo funzionamento e può portare a stati diversi della partita
  * tra diverse istanze dell'applicazione.
  *
  * */

  sealed trait Command
  case class ViewReady(puzzleBoard: PuzzleBoard) extends Command
  case class Update(currentMap: List[Int]) extends Command
  final case class GetValue() extends Command
  private sealed trait InternalCommand extends Command
  private case class InternalUpdateResponse(rsp: Replicator.UpdateResponse[LWWMap[String, List[Int]]]) extends InternalCommand
  private case class InternalGetResponse(rsp: Replicator.GetResponse[LWWMap[String, List[Int]]])
    extends InternalCommand
  private case class InternalSubscribeResponse(chg: Replicator.SubscribeResponse[LWWMap[String, List[Int]]]) extends InternalCommand

  private var puzzleBoard:Option[PuzzleBoard] = None

  def apply(): Behavior[Command] =
    Behaviors.setup[Command] { context =>
      implicit val node: SelfUniqueAddress = DistributedData(context.system).selfUniqueAddress

      // adapter that turns the response messages from the replicator into our own protocol
      DistributedData.withReplicatorMessageAdapter[Command, LWWMap[String, List[Int]]] {
        replicatorAdapter => {

          // Subscribe to changes of the given `key`.
          val key: LWWMapKey[String, List[Int]] = LWWMapKey("map")

          replicatorAdapter.subscribe(key, InternalSubscribeResponse.apply)

          def updated(): Behavior[Command] = Behaviors.receiveMessage[Command] {
            case ViewReady(board) =>
              this.puzzleBoard = Option(board)
              Behaviors.same

            case Update(map) =>
              replicatorAdapter.askUpdate(
                askReplyTo => Replicator.Update(key, LWWMap.empty[String, List[Int]], Replicator.WriteLocal, askReplyTo)
                (x => x.put(node, "map", map)),
                InternalUpdateResponse.apply)
              println("Update request: ", map)
              Behaviors.same

            case GetValue() =>
              replicatorAdapter.askGet(
                askReplyTo => Replicator.Get(key, Replicator.ReadLocal, askReplyTo),
                value => InternalGetResponse(value))

              Behaviors.same

            case internal: InternalCommand =>
              internal match {
                case InternalUpdateResponse(_) =>
                  Behaviors.same


                case InternalGetResponse(rsp @ Replicator.GetSuccess(`key`)) =>
                  /*
                    qui sono ricevute le risposte ai messaggi di GetValue, lettura della variabile condivisa, dove
                    la variabile è inizializzata (esiste una partita in corso)

                    se la struttura distribuita è presente non faccio nulla perchè essa verrà automaticamente ricevuta
                    dal messaggio InternalSubscribeResponse
                   */
                  Behaviors.same

                case InternalGetResponse(value) =>
                  /*
                    qui sono ricevute le risposte ai messaggi di GetValue, lettura della variabile condivisa, dove
                    la variabile non è ancora stata inizializzata (non esiste una partita in corso)

                    se la GetValue ha esito negativo posso creare una nuova partita ed inserirla nel dato condiviso nel
                    cluster.

                    In questo caso infatti non verrà mai ricevuto un messaggio InternalSubscribeResponse visto che
                    non è presente una struttura condivisa
                   */
                  println("Starting a new game", value)
                  puzzleBoard.get.startNewGame()
                  Behaviors.unhandled

                case InternalSubscribeResponse(chg@Replicator.Changed(`key`)) =>
                  /*
                    qui è dove vengono ricevuti tutti i messaggi di modifica avvenuti con successo sulla struttura
                    distribuita
                    */
                  val value = chg.get(key).get("map").get
                  println("Update response: ", value)
                  puzzleBoard.get.updateBoard(value)
                  updated()
              }
          }
          updated()
        }
      }
    }
}