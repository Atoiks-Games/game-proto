/**
 *  Staventure
 *  Copyright (C) 2017-2018  Atoiks-Games <atoiks-games@outlook.com>
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

package org.atoiks.games.staventure.scenes;

import java.awt.Color;
import java.awt.Image;

import java.util.Map;

import org.atoiks.games.framework2d.GameScene;
import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.staventure.prefabs.Player;
import org.atoiks.games.staventure.prefabs.Direction;

import org.atoiks.games.staventure.colliders.CircleCollider;
import org.atoiks.games.staventure.colliders.RectangleCollider;

public final class LibraryScene extends GameScene {

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

    private Image bg;
    private int bgWidth;
    private int bgHeight;

    private Image tableImg;
    private int tableWidth;
    private int tableHeight;
    private final RectangleCollider[] tableColliders = new RectangleCollider[2 * TABLE_XS.length];

    private Image chairImg;
    private int chairWidth;
    private int chairHeight;
    // Each table has two rows of chairs
    private final RectangleCollider[] chairColliders = new RectangleCollider[tableColliders.length * CHAIR_YS.length * 2];

    private Image bookshelfImg;
    private final RectangleCollider[] bookshelfColliders = new RectangleCollider[BOOKSHELF_XS.length];

    private Image comImg;
    private Image comTableImg;
    private CircleCollider comTableCollider;

    private Image comChairImg;
    private final RectangleCollider[] comChairColliders = new RectangleCollider[4];

    private Image officeImg;
    private final RectangleCollider officeCollider = new RectangleCollider();

    private Image fountainImg;
    private final RectangleCollider fountainCollider = new RectangleCollider();
    private final RectangleCollider fountainBumpCollider = new RectangleCollider();

    private Image sofaTableImg;
    private final RectangleCollider sofaTableCollider = new RectangleCollider();

    private Image sofaBigImg;
    private final RectangleCollider sofaBigCollider = new RectangleCollider();

    private Player player;

    private int COLBY_HALLWAY_SCENE_IDX;

    @Override
    public void init() {
        bg = (Image) scene.resources().get("/library/floor.png");
        bgWidth = bg.getWidth(null);
        bgHeight = bg.getHeight(null);

        COLBY_HALLWAY_SCENE_IDX = ((Map<?, Integer>) scene.resources().get("scene.map")).get(ColbyHallwayScene.class);

        tableImg = (Image) scene.resources().get("/library/table.png");
        tableWidth = tableImg.getWidth(null);
        tableHeight = tableImg.getHeight(null);

        for (int i = 0; i < tableColliders.length; ++i) {
            tableColliders[i] = new RectangleCollider(
                    TABLE_XS[i % TABLE_XS.length],
                    i < TABLE_XS.length ? TABLE_Y1 : TABLE_Y2,
                    tableWidth,
                    tableHeight);
        }

        chairImg = (Image) scene.resources().get("/library/chair.png");
        chairWidth = chairImg.getWidth(null);
        chairHeight = chairImg.getHeight(null);

        {
            int i = -1;
            for (final RectangleCollider tc : tableColliders) {
                for (final int ry : CHAIR_YS) {
                    // One chair on each side of table
                    final float cx = tc.x - chairWidth / 2;
                    final float cy = tc.y - TABLE_Y1 + ry;
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

        officeImg = (Image) scene.resources().get("/library/office.png");
        officeCollider.x = 1313;
        officeCollider.y = 365;
        officeCollider.w = officeImg.getWidth(null);
        officeCollider.h = officeImg.getHeight(null);

        fountainImg = (Image) scene.resources().get("/library/fountain.png");
        fountainCollider.x = 1141;
        fountainCollider.y = 165;
        fountainCollider.w = fountainImg.getWidth(null);
        fountainCollider.h = fountainImg.getHeight(null);

        fountainBumpCollider.x = 1227;
        fountainBumpCollider.y = -2;
        fountainBumpCollider.w = 300;
        fountainBumpCollider.h = 2 + fountainCollider.y + fountainCollider.h;

        sofaTableImg = (Image) scene.resources().get("/library/sofa_table.png");
        sofaTableCollider.x = 975;
        sofaTableCollider.y = 95;
        sofaTableCollider.w = sofaTableImg.getWidth(null);
        sofaTableCollider.h = sofaTableImg.getHeight(null);

        sofaBigImg = (Image) scene.resources().get("/library/sofa_big.png");
        sofaBigCollider.x = 975;
        sofaBigCollider.y = 30;
        sofaBigCollider.w = sofaBigImg.getWidth(null);
        sofaBigCollider.h = sofaBigImg.getHeight(null);

        player = new Player();
        player.state = Player.IDLE_FRAME;
        player.speed = 100;
    }

    @Override
    public void enter(int from) {
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

        g.drawImage(bg, 0, 0);

        g.setColor(Color.black);
        g.fillPolygon(fountainBumpCollider.toPolygon());

        for (final RectangleCollider tc : tableColliders) {
            for (final int ry : CHAIR_YS) {
                // One chair on each side of table
                final float cx = tc.x - chairWidth / 2;
                final float cy = tc.y - TABLE_Y1 + ry;
                g.drawImage(chairImg, cx + chairWidth, cy + chairHeight, cx, cy);
                g.drawImage(chairImg, cx + tableWidth, cy);
            }
            g.drawImage(tableImg, tc.x, tc.y);
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

        g.drawImage(officeImg, officeCollider.x, officeCollider.y);
        g.drawImage(fountainImg, fountainCollider.x, fountainCollider.y);
        g.drawImage(sofaTableImg, sofaTableCollider.x, sofaTableCollider.y);
        g.drawImage(sofaBigImg, sofaBigCollider.x, sofaBigCollider.y);

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
        player.update(dt, scene);

        if (oldX != player.x || oldY != player.y) {
            // Dealing with the edges of the map
            if (player.x < -6) player.x = -6;
            if (player.x > bgWidth - 26) {
                if (DOOR_Y1 < player.y && player.y < DOOR_Y2 - 32) {
                    if (player.x > bgWidth) {
                        scene.switchToScene(COLBY_HALLWAY_SCENE_IDX);
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

            if (player.collider.collidesWith(comTableCollider)) {
                player.move(oldX, oldY);
            }

            if (player.collider.collidesWithAny(comChairColliders)) {
                player.move(oldX, oldY);
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