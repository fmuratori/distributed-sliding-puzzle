package part2.akka

import java.awt.Color
import java.awt.event.{MouseAdapter, MouseEvent}
import javax.swing.{BorderFactory, ImageIcon, JButton}

case class TileButton(tile: Tile) extends JButton(new ImageIcon(tile.getImage)) {

  var isButtonSelected: Boolean = false

  addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      isButtonSelected = !isButtonSelected

      if (isButtonSelected) {
        setBorder(BorderFactory.createLineBorder(Color.red, 2))
      } else {
        setBorder(BorderFactory.createLineBorder(Color.gray, 2))
      }
    }
  })
}