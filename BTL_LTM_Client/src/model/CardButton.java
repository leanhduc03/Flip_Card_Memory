package model;
import java.awt.Image;
import javax.swing.*;

public class CardButton extends JButton {
    private final int id;
    private boolean isFlipped = false;
    private boolean isMatched = false;
    private ImageIcon frontImage;
    private static final ImageIcon backImage = new ImageIcon("assets/card_image/back_image.png");
    

    public CardButton(int id, ImageIcon frontImage) {
        this.id = id;
        this.frontImage = scaleImage(frontImage, 100, 100); // Thu nhỏ ảnh về kích thước 80x80 (tùy chỉnh kích thước)
        setIcon(scaleImage(backImage, 100, 100)); // Thu nhỏ mặt sau
    }
    
    // Phương thức để thu nhỏ hình ảnh
    private ImageIcon scaleImage(ImageIcon icon, int w, int h) {
        if (icon == null) {
            return null; // Return null if the icon is null to prevent NullPointerException
        }
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH); // Thu nhỏ ảnh theo kích thước w x h
        return new ImageIcon(scaledImg);
    }

    public int getId() {
        return id;
    }

    public void flip() {
        if (isMatched) return;
        if (isFlipped) {
            setIcon(scaleImage(backImage, 100, 100)); // Hiển thị hình mặt sau nếu lật ngược
        } else {
            setIcon(scaleImage(frontImage, 100, 100)); // Hiển thị hình mặt trước nếu lật lên
        }
        isFlipped = !isFlipped;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
        if (matched) {
            setEnabled(false);
        }
    }
}
