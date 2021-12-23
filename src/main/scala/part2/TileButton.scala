package part2

import java.awt.Color
import java.awt.event.{MouseAdapter, MouseEvent}
import javax.swing.{BorderFactory, ImageIcon, JButton}

case class TileButton(tile: Tile) extends JButton(new ImageIcon(tile.getImage)) {
  addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit =
      setBorder(BorderFactory.createLineBorder(Color.red, 2))
  })
}