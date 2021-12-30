package part2.akka

import java.awt.Image

class Tile(var image: Image, var originalPosition: Int, var currentPosition: Int) extends Comparable[Tile] {
  def getImage: Image = image

  def isInRightPlace: Boolean = currentPosition == originalPosition

  def getCurrentPosition: Int = currentPosition

  def setCurrentPosition(newPosition: Int): Unit = {
    currentPosition = newPosition
  }

  override def compareTo(other: Tile): Int = if (this.currentPosition < other.currentPosition) -1
    else if (this.currentPosition == other.currentPosition) 0
    else 1
}
