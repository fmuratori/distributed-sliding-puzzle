package part2

object Application {

  def main(args: Array[String]): Unit = {
    val n = 2
    val m = 2

    val imagePath = "src/main/resources/bletchley-park-mansion.jpg"

    val puzzle = new PuzzleBoard(n, m, imagePath)
    puzzle.setVisible(true)
  }
}
