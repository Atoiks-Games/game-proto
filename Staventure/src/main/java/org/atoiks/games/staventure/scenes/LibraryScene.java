package org.atoiks.games.staventure.scenes;

import java.awt.Color;
import java.awt.Image;

import org.atoiks.games.framework2d.GameScene;
import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.staventure.prefabs.Player;

import org.atoiks.games.staventure.colliders.CircleCollider;
import org.atoiks.games.staventure.colliders.RectangleCollider;

public final class LibraryScene extends GameScene {

    private static final int TABLE_Y = 90;

    private static final int[] TABLE_XS = {
        90, 240, 390, 540, 690
    };

    private static final int[] CHAIR_YS = {
        105, 173, 231, 299
    };

    private static final int[] BOOKSHELF_XS = {
        90, 260, 430, 600
    };

    private static final int[] BOOKSHELF_YS = {
        475, 475, 475, 475
    };

    private static final int COM_TABLE_X = 800;
    private static final int COM_TABLE_Y = 520;

    private static final int[] COM_CHAIR_XS = {
        644 + 250, 520 + 250, 700 + 250, 810 + 250
    };

    private static final int[] COM_CHAIR_YS = {
        512 - 17, 678 - 17, 804 - 17, 632 - 17
    };

    private static final float SGN_ARRAY[] = {
        1, 0, 0, -1, -1, 0, 0, 1
    };

    private Image bg;
    private int bgWidth;
    private int bgHeight;

    private Image tableImg;
    private int tableWidth;
    private int tableHeight;

    private final RectangleCollider[] tableColliders = new RectangleCollider[TABLE_XS.length];

    private Image chairImg;
    private int chairWidth;
    private int chairHeight;

    // Each table has two rows of chairs
    private final RectangleCollider[] chairColliders = new RectangleCollider[TABLE_XS.length * CHAIR_YS.length * 2];

    private Image bookshelfImg;

    private final RectangleCollider[] bookshelfColliders = new RectangleCollider[BOOKSHELF_XS.length];

    private Image comImg;
    private Image comTableImg;

    private CircleCollider comTableCollider;

    private Image comChairImg;

    private final RectangleCollider[] comChairColliders = new RectangleCollider[4];

    private Player player;

    @Override
    public void init() {
        bg = (Image) scene.resources().get("/library/floor.png");
        bgWidth = bg.getWidth(null);
        bgHeight = bg.getHeight(null);

        tableImg = (Image) scene.resources().get("/library/table.png");
        tableWidth = tableImg.getWidth(null);
        tableHeight = tableImg.getHeight(null);

        for (int i = 0; i < tableColliders.length; ++i) {
            tableColliders[i] = new RectangleCollider(TABLE_XS[i], TABLE_Y, tableWidth, tableHeight);
        }

        chairImg = (Image) scene.resources().get("/library/chair.png");
        chairWidth = chairImg.getWidth(null);
        chairHeight = chairImg.getHeight(null);

        {
            int i = -1;
            for (final int tx : TABLE_XS) {
                // Paint chairs first!
                for (final int cy : CHAIR_YS) {
                    // One chair on each side of table
                    final int cx = tx - chairWidth / 2;
                    chairColliders[++i] = new RectangleCollider(cx, cy, chairWidth, chairHeight);
                    chairColliders[++i] = new RectangleCollider(cx + tableWidth, cy, chairWidth, chairHeight);
                }
            }
        }

        bookshelfImg = (Image) scene.resources().get("/library/bookshelf.png");
        final int bookshelfWidth = bookshelfImg.getWidth(null);
        final int bookshelfHeight = bookshelfImg.getHeight(null);
        for (int i = 0; i < bookshelfColliders.length; ++i) {
            bookshelfColliders[i] = new RectangleCollider(BOOKSHELF_XS[i], BOOKSHELF_YS[i], bookshelfWidth, bookshelfHeight);
        }

        comImg = (Image) scene.resources().get("/library/com.png");
        comTableImg = (Image) scene.resources().get("/library/com_table.png");
        final float comTableR = comTableImg.getHeight(null) / 2;
        comTableCollider = new CircleCollider(COM_TABLE_X + comTableR, COM_TABLE_Y + comTableR, comTableR);

        comChairImg = (Image) scene.resources().get("/library/com_chair.png");
        final int comChairWidth = comChairImg.getWidth(null);
        final int comChairHeight = comChairImg.getHeight(null);
        for (int i = 0; i < comChairColliders.length; ++i) {
            // This loop and SGN_ARRAY is done through trial and error
            // There should be a trig function to do this directly though...
            comChairColliders[i] = new RectangleCollider(
                    COM_CHAIR_XS[i],
                    COM_CHAIR_YS[i],
                    (float) (comChairWidth * SGN_ARRAY[2 * i] - comChairHeight * SGN_ARRAY[2 * i + 1]),
                    (float) (comChairWidth * SGN_ARRAY[2 * i + 1] + comChairHeight * SGN_ARRAY[2 * i]));
        }

        player = new Player();
        player.state = Player.IDLE_FRAME;
        player.direction = Player.Direction.DOWN;
        player.move(10, 15);
        player.speed = 50;
    }

    @Override
    public void render(final IGraphics g) {
        g.setClearColor(Color.black);
        g.clearGraphics();

        // Ask Jeff or someone good at math to model
        // a floor-function that translate every so
        // often as opposed to 24/7?
        g.translate(350 - player.x, 175 - player.y);

        g.drawImage(bg, 0, 0);

        for (final int tx : TABLE_XS) {
            // Paint chairs first!
            for (final int cy : CHAIR_YS) {
                // One chair on each side of table
                final int cx = tx - chairWidth / 2;
                g.drawImage(chairImg, cx + chairWidth, cy + chairHeight, cx, cy);
                g.drawImage(chairImg, cx + tableWidth, cy);
            }
            g.drawImage(tableImg, tx, TABLE_Y);
        }

        for (int i = 0; i < BOOKSHELF_XS.length; ++i) {
            g.drawImage(bookshelfImg, BOOKSHELF_XS[i], BOOKSHELF_YS[i]);
        }

        // Holy cow rotate is a bizarre operation...
        for (int i = 0; i < COM_CHAIR_XS.length; ++i) {
            final float cx = COM_CHAIR_XS[i];
            final float cy = COM_CHAIR_YS[i];
            g.rotate(-i * (float) Math.PI / 2, cx, cy);
            g.drawImage(comChairImg, cx, cy);
            g.rotate(+i * (float) Math.PI / 2, cx, cy);
        }

        g.drawImage(comTableImg, COM_TABLE_X, COM_TABLE_Y);
        g.drawImage(comImg, COM_TABLE_X + 18, COM_TABLE_Y + 18);

        player.render(g);

        /*
        // Debug use: renders the colliders for tables and chairs
        g.setColor(Color.black);
        player.collider.render(g);
        for (int i = 0; i < tableColliders.length; ++i) {
            tableColliders[i].render(g);
        }
        for (int i = 0; i < chairColliders.length; ++i) {
            chairColliders[i].render(g);
        }
        for (int i = 0; i < bookshelfColliders.length; ++i) {
            bookshelfColliders[i].render(g);
        }
        comTableCollider.render(g);
        for (int i = 0; i < comChairColliders.length; ++i) {
            comChairColliders[i].render(g);
        }
        */
    }

    @Override
    public boolean update(final float dt) {
        final float oldX = player.x;
        final float oldY = player.y;
        player.update(dt, scene);

        if (oldX != player.x || oldY != player.y) {
            // Dealing with the edges of the map
            if (player.x < -6) player.x = -6;
            if (player.x > bgWidth - 26) player.x = bgWidth - 26;
            if (player.y < 0) player.y = 0;
            if (player.y > bgHeight - 32) player.y = bgHeight - 32;

            // Dealing with tables
            if (player.collider.collidesWithAny(tableColliders)) {
                player.move(oldX, oldY);
            }

            // Dealing with chairs
            if (player.collider.collidesWithAny(chairColliders)) {
                player.move(oldX, oldY);
            }

            // Dealing with bookshelf
            if (player.collider.collidesWithAny(bookshelfColliders)) {
                player.move(oldX, oldY);
            }

            // Dealing with computer table
            if (player.collider.collidesWith(comTableCollider)) {
                player.move(oldX, oldY);
            }

            // Dealing with computer chairs
            if (player.collider.collidesWithAny(comChairColliders)) {
                player.move(oldX, oldY);
            }
        }
        return true;
    }

    @Override
    public void resize(int w, int h) {
        // Again, ignore
    }
}