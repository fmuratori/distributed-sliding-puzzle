package part2.message;

import part2.view.PuzzleBoardI;

public class UIInitializedMessage implements Message {
    private final PuzzleBoardI puzzleBoard;

    public UIInitializedMessage(PuzzleBoardI puzzleBoard) {
        this.puzzleBoard = puzzleBoard;
    }

    public PuzzleBoardI getPuzzleBoard() {
        return this.puzzleBoard;
    }
}
