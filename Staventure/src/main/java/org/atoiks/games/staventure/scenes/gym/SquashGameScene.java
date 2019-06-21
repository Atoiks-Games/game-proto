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

package org.atoiks.games.staventure.scenes.gym;

import java.awt.Color;
import java.awt.Image;

import java.util.Map;
import java.util.Random;

import javax.swing.JOptionPane;

import org.atoiks.games.framework2d.GameScene;
import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.staventure.GameData;

import org.atoiks.games.staventure.prefabs.Player;
import org.atoiks.games.staventure.prefabs.Direction;
import org.atoiks.games.staventure.prefabs.SquashPlayer;

import org.atoiks.games.staventure.colliders.Collider;
import org.atoiks.games.staventure.colliders.CircleCollider;

public final class SquashGameScene extends GameScene {

    private static final int WINNING_SCORE = 11;

    private static final float RAD_180 = (float) Math.PI;
    private static final float RAD_90  = RAD_180 / 2;
    private static final float RAD_45  = RAD_90 / 2;

    private final Random rnd = new Random();

    private Image bg;

    private Player player;
    private SquashPlayer py;

    private float speed;    // >= 0
    private float angle;    // radians
    private final CircleCollider ball = new CircleCollider();
    private Collider lastCollision = null;

    private int scorePY;
    private int scorePlayer;

    private int SQUASH_COURT_SCENE_IDX;

    private GameData gameData;

    @Override
    public void init() {
        bg = (Image) scene.resources().get("/gym/squash_game/floor.png");

        SQUASH_COURT_SCENE_IDX = ((Map<?, Integer>) scene.resources().get("scene.map")).get(SquashCourtScene.class);

        gameData = (GameData) scene.resources().get("save.dat");

        player = new Player();
        player.state = Player.IDLE_FRAME;
        player.speed = 70;

        py = new SquashPlayer();
        py.state = SquashPlayer.IDLE_FRAME;
        py.speed = 70;

        // ball's r is always 6
        ball.r = 6;
    }

    @Override
    public void enter(int from) {
        resetRound();
        scorePlayer = 0;
        scorePY = 0;
    }

    private void resetRound() {
        // Player starts from the right area
        player.direction = Direction.LEFT;
        player.move(400, 120);

        // PY starts from the left area
        py.direction = Direction.LEFT;
        py.move(400, 298);

        // Not that this scene will be used more than once...
        // but just in case...
        ball.x = 350;
        ball.y = 225;
        lastCollision = null;
        resetBallVelocity();
    }

    private void resetBallVelocity() {
        speed = 90;
        angle = RAD_180;
    }

    @Override
    public void render(IGraphics g) {
        g.drawImage(bg, 0, 0);

        g.setColor(lastCollision == null ? Color.black : (lastCollision == py.collider ? Color.blue : Color.green));
        g.fillCircle((int) ball.x, (int) ball.y, (int) ball.r);

        player.render(g);
        py.render(g);

        /*
        // Debug use: shows collision boxes
        g.setColor(Color.red);
        ball.render(g);
        player.collider.render(g);
        py.collider.render(g);
        */
    }

    @Override
    public boolean update(final float dt) {
        // Update player's position
        player.update(dt);
        if (player.x < -6) player.setX(-6);
        if (player.x > 700 - 26) player.setX(700 - 26);
        if (player.y < 0) player.setY(0);
        if (player.y > 450 - 32) player.setY(450 - 32);

        // Update ball's position
        ball.x += Math.cos(angle) * speed * dt;
        ball.y += Math.sin(angle) * speed * dt;

        if (ball.x < 1 + ball.r) {
            angle = RAD_180 - angle;
            speed -= 0.5;
            ball.x = 1 + ball.r;
        }

        if (ball.y < 1 + ball.r) {
            angle *= -1;
            speed -= 0.5;
            ball.y = 1 + ball.r;
        }

        if (ball.x > 700 - ball.r) {
            // Score!
            if (lastCollision == player.collider) {
                ++scorePlayer;
            } else if (lastCollision == py.collider) {
                ++scorePY;
            }
            resetRound();
            return true;
        }

        if (ball.y > 450 - ball.r) {
            angle *= -1;
            speed -= 0.1;
            ball.y = 450 - ball.r;
        }

        if (scorePY == WINNING_SCORE) {
            JOptionPane.showMessageDialog(null, "GET REKT!");
            return false;
        }

        if (scorePlayer == WINNING_SCORE - 1) {
            // PY speeds up when we are winning
            // this seemed broken, but oh well
            py.speed *= 2.5;
        }

        if (scorePlayer == WINNING_SCORE) {
            gameData.winAgainstPY = true;
            scene.switchToScene(SQUASH_COURT_SCENE_IDX);
            return true;
        }

        // Update PY's movement based on ball's location!
        Direction dir = null;
        if (lastCollision != py.collider) {
            final float dx = ball.x - py.x;
            final float dy = ball.y - py.y;
            final double targetAngle = Math.atan2(dy, dx);
            py.translate(
                    dt * py.speed * (float) Math.cos(targetAngle),
                    dt * py.speed * (float) Math.sin(targetAngle));
            if (Math.abs(dx) > Math.abs(dy)) {
                dir = dx > 0 ? Direction.RIGHT : Direction.LEFT;
            } else {
                dir = dy > 0 ? Direction.DOWN : Direction.UP;
            }
        }
        py.update(dt, dir);

        // Test if ball collides with anyone
        if (ball.collidesWith(py.collider)) onCollideWith(py.collider);
        if (ball.collidesWith(player.collider)) onCollideWith(player.collider);

        return true;
    }

    private void onCollideWith(Collider col) {
        lastCollision = col;
        angle += RAD_45 + RAD_90 * rnd.nextFloat();
        speed += 2;
    }

    @Override
    public void resize(int w, int h) {
        // Ignore
    }
}
