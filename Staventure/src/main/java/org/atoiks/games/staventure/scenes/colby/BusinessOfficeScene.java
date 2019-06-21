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
import java.awt.Image;

import java.util.Map;

import org.atoiks.games.framework2d.Scene;
import org.atoiks.games.framework2d.IGraphics;
import org.atoiks.games.framework2d.SceneManager;
import org.atoiks.games.framework2d.ResourceManager;

import org.atoiks.games.staventure.prefabs.Player;
import org.atoiks.games.staventure.prefabs.Direction;

import org.atoiks.games.staventure.colliders.RectangleCollider;

public final class BusinessOfficeScene implements Scene {

    private static final int DOOR_X1 = 126;
    private static final int DOOR_Y1 = 205;
    private static final int DOOR_X2 = 131;
    private static final int DOOR_Y2 = 245;

    private final Image bgImg;

    private final Image tableImg;
    private final RectangleCollider tableCollider = new RectangleCollider();

    private final Image chairInImg;
    private final RectangleCollider chairInCollider = new RectangleCollider();

    private final Image chairOutImg;
    private final RectangleCollider chairOutCollider = new RectangleCollider();

    private final Image shelfImg;
    private final RectangleCollider shelfCollider = new RectangleCollider();

    private final Image printerImg;
    private final RectangleCollider printerCollider = new RectangleCollider();

    private Player player;

    private int COLBY_HALLWAY_SCENE_IDX;

    public BusinessOfficeScene() {
        this.bgImg = ResourceManager.get("/colby/business_office/floor.png");

        this.tableImg = ResourceManager.get("/colby/business_office/table.png");
        tableCollider.x = 206 + 125;
        tableCollider.y = 54;
        tableCollider.w = this.tableImg.getWidth(null);
        tableCollider.h = this.tableImg.getHeight(null);

        this.chairInImg = ResourceManager.get("/colby/business_office/chair_in.png");
        chairInCollider.x = 319 + 125;
        chairInCollider.y = 167;
        chairInCollider.w = this.chairInImg.getWidth(null);
        chairInCollider.h = this.chairInImg.getHeight(null);

        this.chairOutImg = ResourceManager.get("/colby/business_office/chair_out.png");
        chairOutCollider.x = 119 + 125;
        chairOutCollider.y = 185;
        chairOutCollider.w = this.chairOutImg.getWidth(null);
        chairOutCollider.h = this.chairOutImg.getHeight(null);

        this.shelfImg = ResourceManager.get("/colby/business_office/shelf.png");
        shelfCollider.x = 15 + 125;
        shelfCollider.y = 15;
        shelfCollider.w = this.shelfImg.getWidth(null);
        shelfCollider.h = this.shelfImg.getHeight(null);

        this.printerImg = ResourceManager.get("/colby/business_office/printer.png");
        printerCollider.x = 327 + 125;
        printerCollider.y = 9;
        printerCollider.w = this.printerImg.getWidth(null);
        printerCollider.h = this.printerImg.getHeight(null);

        this.player = new Player();
        this.player.state = Player.IDLE_FRAME;
        this.player.speed = 50;
    }

    @Override
    public void enter(Scene from) {
        player.direction = Direction.RIGHT;
        player.move(145, 210);
    }

    @Override
    public void render(IGraphics g) {
        g.setClearColor(Color.black);
        g.clearGraphics();

        g.drawImage(bgImg, 125, 0);

        g.drawImage(tableImg, tableCollider.x, tableCollider.y);
        g.drawImage(chairInImg, chairInCollider.x, chairInCollider.y);
        g.drawImage(chairOutImg, chairOutCollider.x, chairOutCollider.y);
        g.drawImage(shelfImg, shelfCollider.x, shelfCollider.y);
        g.drawImage(printerImg, printerCollider.x, printerCollider.y);

        player.render(g);

        g.setColor(Color.red);
        g.fillRect(DOOR_X1, DOOR_Y1, DOOR_X2, DOOR_Y2);
        g.setColor(Color.black);
        g.fillRect(DOOR_X1 - 32, DOOR_Y1, DOOR_X1, DOOR_Y2);
    }

    @Override
    public boolean update(float dt) {
        final float oldX = player.x;
        final float oldY = player.y;
        player.update(dt);

        if (player.y < 5) player.y = 5;
        if (player.y > 450 - 32 - 5) player.y = 450 - 32 - 5;
        if (player.x < 129 - 6) {
            if (DOOR_Y1 < player.y && player.y < DOOR_Y2 - 26) {
                if (player.x < 129 - 28) {
                    SceneManager.swapScene(new ColbyHallwayScene());
                    return true;
                }
            } else {
                player.x = 129 - 6;
            }
        }
        if (player.x > 575 - 32) player.x = 575 - 32;

        if (player.collider.collidesWith(tableCollider)) {
            player.move(oldX, oldY);
        }
        if (player.collider.collidesWith(chairInCollider)) {
            player.move(oldX, oldY);
        }
        if (player.collider.collidesWith(chairOutCollider)) {
            player.move(oldX, oldY);
        }
        if (player.collider.collidesWith(shelfCollider)) {
            player.move(oldX, oldY);
        }
        if (player.collider.collidesWith(printerCollider)) {
            player.move(oldX, oldY);
        }
        return true;
    }

    @Override
    public void resize(int w, int h) {
        //
    }
}
