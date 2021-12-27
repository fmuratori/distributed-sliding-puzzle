package part2test

import akka.actor.typed.ActorRef
import part2test.DataActorListener.{Command, Increment, ViewReady}

import java.awt.event.ActionEvent
import java.awt.image.{BufferedImage, CropImageFilter, FilteredImageSource}
import java.awt.{BorderLayout, Color, GridLayout}
import java.io.{File, IOException}
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
  val selectionManager = new SelectionManager

  board.setBorder(BorderFactory.createLineBorder(Color.gray))
  board.setLayout(new GridLayout(rows, columns, 0, 0))
  getContentPane.add(board, BorderLayout.CENTER)
  createTiles(imagePath)
  paintPuzzle(board)
  actorRef ! ViewReady(this)

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

  def updateBoard(value: List[Int]): Unit = {
    paintPuzzle(board)
    checkSolution()
  }

  private def paintPuzzle(board: JPanel): Unit = {
    board.removeAll()
    tiles = tiles.sortWith((t1:Tile, t2:Tile) => t1.compareTo(t2) < 0)

    tiles.foreach((tile: Tile) => {
      val btn = TileButton(tile)
      board.add(btn)
      btn.setBorder(BorderFactory.createLineBorder(Color.gray))
      btn.addActionListener((_: ActionEvent) => {
        selectionManager.selectTile(tile, () => {
          actorRef ! Increment(tiles
            .sortBy((t:Tile) => t.originalPosition )
            .map(t => t.currentPosition))
        })
      })
    })
    pack()
    setLocationRelativeTo(null)
  }

  private def checkSolution(): Unit = {
    if (tiles.count((t: Tile) => t.isInRightPlace) == tiles.size) JOptionPane.showMessageDialog(this, "Puzzle Completed!", "", JOptionPane.INFORMATION_MESSAGE)
  }
}

