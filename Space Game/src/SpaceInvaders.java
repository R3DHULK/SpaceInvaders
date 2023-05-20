import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SpaceInvaders extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PLAYER_WIDTH = 60;
    private static final int PLAYER_HEIGHT = 40;
    private static final int ENEMY_WIDTH = 40;
    private static final int ENEMY_HEIGHT = 40;
    private static final int BULLET_WIDTH = 10;
    private static final int BULLET_HEIGHT = 20;
    private static final int ENEMY_ROWS = 4;
    private static final int ENEMY_COLS = 8;
    private static final int ENEMY_GAP = 10;
    private static final int ENEMY_INIT_X = 50;
    private static final int ENEMY_INIT_Y = 50;
    private static final int PLAYER_SPEED = 5;
    private static final int BULLET_SPEED = 7;
    private static final int ENEMY_SPEED = 1;

    private boolean isGameOver = false;
    private boolean isGameWon = false;
    private boolean isBulletFired = false;
    private int playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
    private int playerY = HEIGHT - PLAYER_HEIGHT - 20;
    private int bulletX;
    private int bulletY;
    private int enemyDirection = 1;

    private List<Rectangle> enemies = new ArrayList<>();

    public SpaceInvaders() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        initEnemies();

        Timer timer = new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                repaint();
            }
        });
        timer.start();
    }

    private void initEnemies() {
        int startX = ENEMY_INIT_X;
        int startY = ENEMY_INIT_Y;

        for (int row = 0; row < ENEMY_ROWS; row++) {
            for (int col = 0; col < ENEMY_COLS; col++) {
                Rectangle enemy = new Rectangle(startX + col * (ENEMY_WIDTH + ENEMY_GAP), startY + row * (ENEMY_HEIGHT + ENEMY_GAP), ENEMY_WIDTH, ENEMY_HEIGHT);
                enemies.add(enemy);
            }
        }
    }

    private void update() {
        if (!isGameOver && !isGameWon) {
            // Update player position
            if (playerX <= 0) {
                playerX = 0;
            } else if (playerX >= WIDTH - PLAYER_WIDTH) {
                playerX = WIDTH - PLAYER_WIDTH;
            }

            // Update bullet position
            if (isBulletFired) {
                bulletY -= BULLET_SPEED;
                if (bulletY < 0) {
                    isBulletFired = false;
                }
            }

            // Update enemy position
            for (Rectangle enemy : enemies) {
                enemy.x += enemyDirection * ENEMY_SPEED;

                if (enemy.x <= 0 || enemy.x >= WIDTH - ENEMY_WIDTH) {
                    enemyDirection *= -1;
                    for (Rectangle e : enemies) {
                        e.y += ENEMY_HEIGHT;
                    }
                }

                if (enemy.intersects(new Rectangle(bulletX, bulletY, BULLET_WIDTH, BULLET_HEIGHT))) {
                    enemies.remove(enemy);
                    isBulletFired = false;
                    if (enemies.isEmpty()) {
                        isGameWon = true;
                    }
                    break;
                }

                if (enemy.intersects(new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT))) {
                    isGameOver = true;
                    break;
                }
            }
        }
    }

    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT) {
            playerX -= PLAYER_SPEED;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            playerX += PLAYER_SPEED;
        } else if (keyCode == KeyEvent.VK_SPACE && !isBulletFired) {
            bulletX = playerX + PLAYER_WIDTH / 2 - BULLET_WIDTH / 2;
            bulletY = playerY - BULLET_HEIGHT;
            isBulletFired = true;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isGameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", WIDTH / 2 - 100, HEIGHT / 2);
        } else if (isGameWon) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("You Won!", WIDTH / 2 - 80, HEIGHT / 2);
        } else {
            // Draw player
            g.setColor(Color.GREEN);
            g.fillRect(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

            // Draw bullet
            if (isBulletFired) {
                g.setColor(Color.RED);
                g.fillRect(bulletX, bulletY, BULLET_WIDTH, BULLET_HEIGHT);
            }

            // Draw enemies
            g.setColor(Color.BLUE);
            for (Rectangle enemy : enemies) {
                g.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        SpaceInvaders game = new SpaceInvaders();
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
