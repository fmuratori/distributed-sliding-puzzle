package part2.rmi;

import part2.rmi.remote.GameManager;

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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PuzzleBoard extends JFrame {
	
	final int rows, columns;
	private final List<Tile> tiles = new ArrayList<>();
	private final JPanel board;

	private final SelectionManager selectionManager = new SelectionManager();
	
    public PuzzleBoard(final int rows, final int columns, final String imagePath) {
    	this.rows = rows;
		this.columns = columns;
    	
    	setTitle("Puzzle");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        board = new JPanel();
        board.setBorder(BorderFactory.createLineBorder(Color.gray));
        board.setLayout(new GridLayout(rows, columns, 0, 0));
        getContentPane().add(board, BorderLayout.CENTER);
        
        createTiles(imagePath);
        paintPuzzle();

        GameManager.get().setPuzzleBoard(this);
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
    
    private void paintPuzzle() {
    	board.removeAll();
    	
    	Collections.sort(tiles);
    	
    	tiles.forEach(tile -> {
    		final TileButton btn = new TileButton(tile);
            board.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(actionListener -> selectionManager.selectTile(tile,
                    () -> GameManager.get().requestAction()));
    	});
    	
    	pack();
        setLocationRelativeTo(null);
    }

    public void updateBoard(List<Integer> newMap) {
        System.out.println(newMap);

        // reorder tiles
        int i = 0;
        for (int elem: newMap) {
            Optional<Tile> tile = tiles.stream().filter(t -> t.getOriginalPosition() == elem).findFirst();
            if (tile.isPresent())
                tile.get().setCurrentPosition(i++);
        }

        Collections.sort(tiles);
        paintPuzzle();
    }

    public void executeAction() {
        selectionManager.swap();
        paintPuzzle();
        checkSolution();
    }

    public List<Integer> getMap() {
        return tiles.stream().map(Tile::getOriginalPosition).collect(Collectors.toList());
    }

    private void checkSolution() {
    	if(tiles.stream().allMatch(Tile::isInRightPlace)) {
    		JOptionPane.showMessageDialog(this, "Puzzle Completed!", "", JOptionPane.INFORMATION_MESSAGE);
    	}
    }
}
