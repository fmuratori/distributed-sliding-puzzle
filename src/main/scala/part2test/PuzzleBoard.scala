package part2test

import akka.actor.typed.ActorRef
import part2test.DataActorListener.{Command, GetValue, Increment, ViewReady, puzzleBoard}

import java.awt.event.ActionEvent
import java.awt.image.{BufferedImage, CropImageFilter, FilteredImageSource}
import java.awt.{BorderLayout, Color, GridLayout}
import java.io.{File, IOException}
import java.lang.Thread.sleep
import java.util.stream.IntStream
import javax.imageio.ImageIO
import javax.swing._
import scala.util.Random

class PuzzleBoard(val rows: Int, val columns: Int, val imagePath: String, val actorRef: ActorRef[Command]) extends JFrame {
  setTitle("Puzzle")
  setResizable(false)
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  val board = new JPanel
  var tiles:List[Tile] = List()
  var selectedTile: Option[Tile] = None
  val selectionManager = new SelectionManager
  var isExecutingAction = false

  board.setBorder(BorderFactory.createLineBorder(Color.gray))
  board.setLayout(new GridLayout(rows, columns, 0, 0))
  getContentPane.add(board, BorderLayout.CENTER)
  actorRef ! ViewReady(this)
  sleep(10000)
  actorRef ! GetValue()

  private def createTiles(imagePath: String): Unit = {
    var image: BufferedImage = null
    try {
      image = ImageIO.read(new File(imagePath))
    } catch {
      case _: IOException =>
        JOptionPane.showMessageDialog(this, "Could not load image", "Error", JOptionPane.ERROR_MESSAGE)
        return
    }
    val imageWidth = image.getWidth(null)
    val imageHeight = image.getHeight(null)
    var position = 0

    var randomPositions:List[Integer] = List()
    IntStream.range(0, rows * columns).forEach((item: Int) => {
      randomPositions = randomPositions :+ item
    })
    randomPositions = Random.shuffle(randomPositions)

    tiles = List()
    for (i <- 0 until rows) {
      for (j <- 0 until columns) {
        val imagePortion = createImage(new FilteredImageSource(image.getSource, new CropImageFilter(j * imageWidth / columns, i * imageHeight / rows, imageWidth / columns, imageHeight / rows)))
        tiles = tiles :+ new Tile(imagePortion, position, randomPositions(position))
        position += 1
      }
    }
  }

  def updateBoard(newPositions: List[Int]): Unit = {
    if (tiles.isEmpty) {
      println("Loading a game...")
      createTiles(imagePath)
    }

    for ( i <- 0 to 3) {
      tiles.find(t => t.originalPosition == newPositions(i)).get.currentPosition = i
    }

    tiles = tiles.sortWith((t1:Tile, t2:Tile) => t1.compareTo(t2) < 0)

    paintPuzzle(board)
    checkSolution()
    isExecutingAction = false
  }

  def disableBoard(): Unit = {
    isExecutingAction = true
  }

  def startNewGame(): Unit = {
    createTiles(imagePath)

    tiles = tiles.sortWith((t1:Tile, t2:Tile) => t1.compareTo(t2) < 0)
    val newBoard = tiles.map(t => t.originalPosition)
    actorRef ! Increment(newBoard)
  }

  private def paintPuzzle(board: JPanel): Unit = {
    board.removeAll()
    tiles = tiles.sortWith((t1:Tile, t2:Tile) => t1.compareTo(t2) < 0)

    tiles.foreach((tile: Tile) => {
      val btn = TileButton(tile)
      board.add(btn)
      btn.setBorder(BorderFactory.createLineBorder(Color.gray))
      btn.addActionListener((_: ActionEvent) => {
        if (!isExecutingAction) {
          if (selectedTile.isDefined) {
            disableBoard()
            val newBoard = tiles.map(t => t.originalPosition)
              .updated(selectedTile.get.currentPosition, tile.originalPosition)
              .updated(tile.currentPosition, selectedTile.get.originalPosition)
            actorRef ! Increment(newBoard)
            selectedTile = None
            isExecutingAction = true
          } else
            selectedTile = Option(tile)
        }
      })
    })
    pack()
  }

  private def checkSolution(): Unit = {
    if (tiles.count((t: Tile) => t.isInRightPlace) == tiles.size) JOptionPane.showMessageDialog(this, "Puzzle Completed!", "", JOptionPane.INFORMATION_MESSAGE)
  }
}

