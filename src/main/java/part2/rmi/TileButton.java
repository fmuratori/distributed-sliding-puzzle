package part2.rmi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TileButton extends JButton{

	private boolean isSelected = false;

	public TileButton(final Tile tile) {
		super(new ImageIcon(tile.getImage()));
		
		addMouseListener(new MouseAdapter() {            
            @Override
            public void mouseClicked(MouseEvent e) {
            	isSelected = !isSelected;
            	if (isSelected)
            		setBorder(BorderFactory.createLineBorder(Color.red));
        		else
					setBorder(BorderFactory.createLineBorder(Color.gray));

            }
        });
	}
}
