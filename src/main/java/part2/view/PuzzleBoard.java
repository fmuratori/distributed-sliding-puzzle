package part2.view;

import akka.actor.typed.ActorRef;
import part2.actor.GameActor;
import part2.message.Message;
import part2.message.UIInitializedMessage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class PuzzleBoard extends JFrame implements PuzzleBoardI{
	
	final int rows, columns;
	private final List<Tile> tiles = new ArrayList<>();
	
	private final SelectionManager selectionManager = new SelectionManager();
    private final ActorRef<Message> uiActor;
    private final String imagePath;

    public PuzzleBoard(final int rows, final int columns, final String imagePath, final ActorRef<Message> uiActor) {
    	this.rows = rows;
		this.columns = columns;
		this.imagePath = imagePath;
    	this.uiActor = uiActor;

    	this.setView();

        this.uiActor.tell(new UIInitializedMessage(this));
    }

    private void setView() {
        setTitle("Puzzle");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel board = new JPanel();
        board.setBorder(BorderFactory.createLineBorder(Color.gray));
        board.setLayout(new GridLayout(this.rows, this.columns, 0, 0));
        getContentPane().add(board, BorderLayout.CENTER);

        createTiles(this.imagePath);
        paintPuzzle(board);
    }

    
    private void createTiles(final String imagePath) {
		final BufferedImage image;
        
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load image", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);

        int position = 0;
        
        final List<Integer> randomPositions = new ArrayList<>();
        IntStream.range(0, rows*columns).forEach(randomPositions::add);
        Collections.shuffle(randomPositions);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
            	final Image imagePortion = createImage(new FilteredImageSource(image.getSource(),
                        new CropImageFilter(j * imageWidth / columns, 
                        					i * imageHeight / rows, 
                        					(imageWidth / columns), 
                        					imageHeight / rows)));

                tiles.add(new Tile(imagePortion, position, randomPositions.get(position)));
                position++;
            }
        }
	}
    
    private void paintPuzzle(final JPanel board) {
    	board.removeAll();
    	
    	Collections.sort(tiles);
    	
    	tiles.forEach(tile -> {
    		final TileButton btn = new TileButton(tile);
            board.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(actionListener -> selectionManager.selectTile(tile, () -> {
                this.uiActor.tell(new GameActor.Increment());
                paintPuzzle(board);
                checkSolution();
            }));
    	});
    	
    	pack();
        setLocationRelativeTo(null);
    }

    private void checkSolution() {
    	if(tiles.stream().allMatch(Tile::isInRightPlace)) {
    		JOptionPane.showMessageDialog(this, "Puzzle Completed!", "", JOptionPane.INFORMATION_MESSAGE);
    	}
    }
}
