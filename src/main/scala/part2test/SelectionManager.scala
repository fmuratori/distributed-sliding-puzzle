package part2test

class SelectionManager {
  private var selectionActive = false
  private var selectedTile:Tile = null

  def selectTile(tile: Tile, runnable: () => Unit): Unit = {
    if (selectionActive) {
      selectionActive = false
//      swap(selectedTile, tile)
      runnable.apply()
    }
    else {
      selectionActive = true
      selectedTile = tile
    }
  }

  private def swap(t1: Tile, t2: Tile): Unit = {
    val pos = t1.getCurrentPosition
    t1.setCurrentPosition(t2.getCurrentPosition)
    t2.setCurrentPosition(pos)
  }
}

