package part2.rmi;

public class SelectionManager {

	private boolean selectionActive = false;
	private Tile selectedTile;
	private Tile selectedTile2;

	public void selectTile(final Tile tile, final Runnable runnable) {
		
		if(selectionActive) {
			if (tile.equals(selectedTile)) {
				selectedTile = null;
				selectionActive = false;
			} else {
				selectionActive = false;
				selectedTile2 = tile;
				runnable.run();
			}
		} else {
			selectionActive = true;
			selectedTile = tile;
		}
	}

	public void swap() {
		int pos = selectedTile.getCurrentPosition();
		selectedTile.setCurrentPosition(selectedTile2.getCurrentPosition());
		selectedTile2.setCurrentPosition(pos);
	}

}
