/**
 *  Staventure
 *  Copyright (C) 2017-2019  Atoiks-Games <atoiks-games@outlook.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.atoiks.games.staventure.scenes.colby;

import java.awt.Color;

import javax.swing.JOptionPane;

import org.atoiks.games.framework2d.Scene;
import org.atoiks.games.framework2d.IGraphics;
import org.atoiks.games.framework2d.SceneManager;
import org.atoiks.games.framework2d.ResourceManager;

import org.atoiks.games.framework2d.resource.Texture;

import org.atoiks.games.staventure.prefabs.Player;
import org.atoiks.games.staventure.prefabs.Direction;

import org.atoiks.games.staventure.scenes.SavePointScene;

import org.atoiks.games.staventure.colliders.CircleCollider;
import org.atoiks.games.staventure.colliders.RectangleCollider;

public final class LibraryScene implements Scene {

    private static final int DOOR_Y1 = 253;
    private static final int DOOR_Y2 = 365;

    private static final int TABLE_Y1 = 50;
    private static final int TABLE_Y2 = 330;

    private static final int[] TABLE_XS = {
        240, 390, 540, 690
    };

    private static final int[] CHAIR_YS = {
        90, 138, 186, 234
    };

    private static final int[] BOOKSHELF_XS = {
        90, 260, 430, 600, 770, 1439,
        90, 260, 430, 600, 770, 1439,
    };

    private static final int[] BOOKSHELF_YS = {
        600, 600, 600, 600, 600, 590,
        890, 890, 890, 890, 890, 860,
    };

    private static final int COM_TABLE_X = 1000;
    private static final int COM_TABLE_Y = 640;

    private static final int[] COM_CHAIR_XS = {
        644 + 450, 520 + 450, 700 + 450, 810 + 450
    };

    private static final int[] COM_CHAIR_YS = {
        512 - 17 + 120, 678 - 17 + 120, 804 - 17 + 120, 632 - 17 + 120
    };

    private static final float SGN_ARRAY[] = {
        1, 0, 0, -1, -1, 0, 0, 1
    };

    private final Texture bg;
    private final int bgWidth;
    private final int bgHeight;

    private final Texture tableImg;
    private final int tableWidth;
    private final int tableHeight;
    private final RectangleCollider[] tableColliders = new RectangleCollider[2 * TABLE_XS.length];

    private final Texture chairImg;
    private final int chairWidth;
    private final int chairHeight;
    // Each table has two rows of chairs
    private final RectangleCollider[] chairColliders = new RectangleCollider[tableColliders.length * CHAIR_YS.length * 2];

    private final Texture bookshelfImg;
    private final RectangleCollider[] bookshelfColliders = new RectangleCollider[BOOKSHELF_XS.length];

    private final Texture comImg;
    private final Texture comTableImg;
    private final CircleCollider comTableCollider;

    private final Texture comChairImg;
    private final RectangleCollider[] comChairColliders = new RectangleCollider[4];

    private final Texture officeImg;
    private final RectangleCollider officeCollider = new RectangleCollider();

    private final Texture fountainImg;
    private final RectangleCollider fountainCollider = new RectangleCollider();
    private final RectangleCollider fountainBumpCollider = new RectangleCollider();

    private final Texture sofaTableImg;
    private final RectangleCollider sofaTableCollider = new RectangleCollider();

    private final Texture sofaBigImg;
    private final RectangleCollider sofaBigCollider = new RectangleCollider();

    private final Player player;

    public LibraryScene() {
        this.bg = ResourceManager.get("/colby/library/floor.png");
        this.bgWidth = bg.getWidth();
        this.bgHeight = bg.getHeight();

        this.tableImg = ResourceManager.get("/colby/library/table.png");
        this.tableWidth = tableImg.getWidth();
        this.tableHeight = tableImg.getHeight();

        for (int i = 0; i < tableColliders.length; ++i) {
            this.tableColliders[i] = new RectangleCollider(
                    TABLE_XS[i % TABLE_XS.length],
                    i < TABLE_XS.length ? TABLE_Y1 : TABLE_Y2,
                    tableWidth,
                    tableHeight);
        }

        this.chairImg = ResourceManager.get("/colby/library/chair.png");
        this.chairWidth = chairImg.getWidth();
        this.chairHeight = chairImg.getHeight();

        {
            int i = -1;
            for (final RectangleCollider tc : tableColliders) {
                for (final int ry : CHAIR_YS) {
                    // One chair on each side of table
                    final float cx = tc.x - this.chairWidth / 2;
                    final float cy = tc.y - TABLE_Y1 + ry;
                    this.chairColliders[++i] = new RectangleCollider(cx, cy, chairWidth, chairHeight);
                    this.chairColliders[++i] = new RectangleCollider(cx + tableWidth, cy, chairWidth, chairHeight);
                }
            }
        }

        this.bookshelfImg = ResourceManager.get("/colby/library/bookshelf.png");
        final int bookshelfWidth = bookshelfImg.getWidth();
        final int bookshelfHeight = bookshelfImg.getHeight();
        for (int i = 0; i < bookshelfColliders.length; ++i) {
            bookshelfColliders[i] = new RectangleCollider(BOOKSHELF_XS[i], BOOKSHELF_YS[i], bookshelfWidth, bookshelfHeight);
        }

        this.comImg = ResourceManager.get("/colby/library/com.png");
        this.comTableImg = ResourceManager.get("/colby/library/com_table.png");
        final float comTableR = comTableImg.getHeight() / 2;
        comTableCollider = new CircleCollider(COM_TABLE_X + comTableR, COM_TABLE_Y + comTableR, comTableR);

        this.comChairImg = ResourceManager.get("/colby/library/com_chair.png");
        final int comChairWidth = comChairImg.getWidth();
        final int comChairHeight = comChairImg.getHeight();
        for (int i = 0; i < comChairColliders.length; ++i) {
            // This loop and SGN_ARRAY is done through trial and error
            // There should be a trig function to do this directly though...
            comChairColliders[i] = new RectangleCollider(
                    COM_CHAIR_XS[i],
                    COM_CHAIR_YS[i],
                    (float) (comChairWidth * SGN_ARRAY[2 * i] - comChairHeight * SGN_ARRAY[2 * i + 1]),
                    (float) (comChairWidth * SGN_ARRAY[2 * i + 1] + comChairHeight * SGN_ARRAY[2 * i]));
        }

        this.officeImg = ResourceManager.get("/colby/library/office.png");
        officeCollider.x = 1313;
        officeCollider.y = 365;
        officeCollider.w = officeImg.getWidth();
        officeCollider.h = officeImg.getHeight();

        this.fountainImg = ResourceManager.get("/colby/library/fountain.png");
        fountainCollider.x = 1141;
        fountainCollider.y = 165;
        fountainCollider.w = fountainImg.getWidth();
        fountainCollider.h = fountainImg.getHeight();

        fountainBumpCollider.x = 1227;
        fountainBumpCollider.y = -2;
        fountainBumpCollider.w = 300;
        fountainBumpCollider.h = 2 + fountainCollider.y + fountainCollider.h;

        this.sofaTableImg = ResourceManager.get("/colby/library/sofa_table.png");
        sofaTableCollider.x = 975;
        sofaTableCollider.y = 98;
        sofaTableCollider.w = sofaTableImg.getWidth();
        sofaTableCollider.h = sofaTableImg.getHeight();

        this.sofaBigImg = ResourceManager.get("/colby/library/sofa_big.png");
        sofaBigCollider.x = 975;
        sofaBigCollider.y = 33;
        sofaBigCollider.w = sofaBigImg.getWidth();
        sofaBigCollider.h = sofaBigImg.getHeight();

        this.player = new Player();
        this.player.state = Player.IDLE_FRAME;
        this.player.speed = 100;
    }

    @Override
    public void enter(Scene from) {
        player.direction = Direction.LEFT;
        player.move(1465, 280);
    }

    @Override
    public void render(final IGraphics g) {
        g.setClearColor(Color.black);
        g.clearGraphics();

        // Ask Jeff or someone good at math to model
        // a floor-function that translate every so
        // often as opposed to 24/7?
        g.translate(350 - player.x, 175 - player.y);

        g.drawTexture(bg, 0, 0);

        g.setColor(Color.black);
        g.fillPolygon(fountainBumpCollider.toPolygon());

        for (final RectangleCollider tc : tableColliders) {
            for (final int ry : CHAIR_YS) {
                // One chair on each side of table
                final float cx = tc.x - chairWidth / 2;
                final float cy = tc.y - TABLE_Y1 + ry;
                g.drawTexture(chairImg, cx + chairWidth, cy + chairHeight, cx, cy);
                g.drawTexture(chairImg, cx + tableWidth, cy);
            }
            g.drawTexture(tableImg, tc.x, tc.y);
        }

        for (int i = 0; i < BOOKSHELF_XS.length; ++i) {
            g.drawTexture(bookshelfImg, BOOKSHELF_XS[i], BOOKSHELF_YS[i]);
        }

        // Holy cow rotate is a bizarre operation...
        for (int i = 0; i < COM_CHAIR_XS.length; ++i) {
            final float cx = COM_CHAIR_XS[i];
            final float cy = COM_CHAIR_YS[i];
            g.rotate(-i * (float) Math.PI / 2, cx, cy);
            g.drawTexture(comChairImg, cx, cy);
            g.rotate(+i * (float) Math.PI / 2, cx, cy);
        }

        g.drawTexture(comTableImg, COM_TABLE_X, COM_TABLE_Y);
        g.drawTexture(comImg, COM_TABLE_X + 18, COM_TABLE_Y + 18);

        g.drawTexture(officeImg, officeCollider.x, officeCollider.y);
        g.drawTexture(fountainImg, fountainCollider.x, fountainCollider.y);
        g.drawTexture(sofaTableImg, sofaTableCollider.x, sofaTableCollider.y);
        g.drawTexture(sofaBigImg, sofaBigCollider.x, sofaBigCollider.y);

        player.render(g);

        g.setColor(Color.red);
        g.fillRect(1495, DOOR_Y1, 1500, DOOR_Y2);
        g.setColor(Color.black);
        g.fillRect(1500, DOOR_Y1, 1550, DOOR_Y2);
    }

    @Override
    public boolean update(final float dt) {
        final float oldX = player.x;
        final float oldY = player.y;
        player.update(dt);

        if (oldX != player.x || oldY != player.y) {
            // Dealing with the edges of the map
            if (player.x < -6) player.x = -6;
            if (player.x > bgWidth - 26) {
                if (DOOR_Y1 < player.y && player.y < DOOR_Y2 - 32) {
                    if (player.x > bgWidth) {
                        SceneManager.swapScene(new ColbyHallwayScene());
                        return true;
                    }
                } else {
                    player.x = bgWidth - 26;
                }
            }
            if (player.y < 0) player.y = 0;
            if (player.y > bgHeight - 32) player.y = bgHeight - 32;

            if (player.collider.collidesWithAny(tableColliders)) {
                player.move(oldX, oldY);
            }

            if (player.collider.collidesWithAny(chairColliders)) {
                player.move(oldX, oldY);
            }

            if (player.collider.collidesWithAny(bookshelfColliders)) {
                player.move(oldX, oldY);
            }

            if (player.collider.collidesWith(comTableCollider) || player.collider.collidesWithAny(comChairColliders)) {
                player.move(oldX, oldY);
                SceneManager.pushScene(new SavePointScene());
                return true;
            }

            if (player.collider.collidesWith(officeCollider)) {
                player.move(oldX, oldY);
            }

            if (player.collider.collidesWith(fountainCollider)) {
                player.move(oldX, oldY);
            }

            if (player.collider.collidesWith(fountainBumpCollider)) {
                player.move(oldX, oldY);
            }

            if (player.collider.collidesWith(sofaTableCollider)) {
                player.move(oldX, oldY);
            }

            if (player.collider.collidesWith(sofaBigCollider)) {
                player.direction = Direction.DOWN;
                player.move(sofaBigCollider.x + 40, sofaBigCollider.y + 25);
                JOptionPane.showMessageDialog(null, "GET CONSUMED BY BIG COMFY SOFA BOIII!");
            }
        }
        return true;
    }

    @Override
    public void resize(int w, int h) {
        // Again, ignore
    }
}
