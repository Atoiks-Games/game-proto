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

package org.atoiks.games.staventure.scenes;

import java.io.IOException;

import java.awt.Color;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.atoiks.games.framework2d.Scene;
import org.atoiks.games.framework2d.IGraphics;
import org.atoiks.games.framework2d.SceneManager;
import org.atoiks.games.framework2d.ResourceManager;

import org.atoiks.games.framework2d.decoder.AudioDecoder;
import org.atoiks.games.framework2d.decoder.DecodeException;
import org.atoiks.games.framework2d.decoder.SerializableDecoder;

import org.atoiks.games.framework2d.resolver.ExternalResourceResolver;

import org.atoiks.games.staventure.GameData;

import org.atoiks.games.staventure.scenes.gym.SquashCourtScene;

import org.atoiks.games.staventure.prefabs.*;

import static org.atoiks.games.staventure.Main.WIDTH;

public final class LoadingScene implements Scene {

    private enum LoadState {
        WAITING, LOADING, AWAIT_FUTURE, DONE, NO_RES;
    }

    private static final Color[] COLORS = {
        Color.red, Color.yellow, Color.green
    };

    private final ExecutorService loader = Executors.newSingleThreadExecutor();

    private LoadState loaded = LoadState.WAITING;

    private float elapsed;

    @Override
    public void resize(int w, int h) {
        // Ignore
    }

    @Override
    public void render(final IGraphics g) {
        g.setClearColor(Color.black);
        g.clearGraphics();

        for (int i = 0; i < COLORS.length; ++i) {
            g.setColor(COLORS[i]);
            final double k = elapsed - 2 * Math.PI * i;
            g.fillCircle((float) (k * 100), (float) (Math.cos(k * 5) * 100) + 300, 30);
        }
    }

    @Override
    public boolean update(final float dt) {
        elapsed += dt;
        if ((elapsed - 2 * Math.PI * COLORS.length) * 100 > WIDTH) {
            elapsed = 0;
        }

        switch (loaded) {
            case NO_RES:
                return false;
            case LOADING:
                break;
            case AWAIT_FUTURE:
                if (!ResourceManager.hasTasksRemaining()) {
                    // At this point, all images would have been completely loaded

                    // Player resources
                    for (int i = 1; i <= 12; ++i) {
                        Player.SPRITE_SHEET[i - 1] = ResourceManager.get("/main_char/spr_" + i + ".png");
                    }

                    // PY resources
                    for (int i = 1; i <= 12; ++i) {
                        SquashPlayer.SPRITE_SHEET[i - 1] = ResourceManager.get("/py/spr_" + i + ".png");
                    }

                    loaded = LoadState.DONE;
                }
                break;
            case DONE:
                loader.shutdown();
                SceneManager.swapScene(new SquashCourtScene(SquashCourtScene.ID.TOP));
                break;
            case WAITING:
                loaded = LoadState.LOADING;
                loader.submit(() -> {
                    try {
                        // Player resources
                        for (int i = 1; i <= 12; ++i) {
                            loadImageFromResource("/main_char/spr_" + i + ".png");
                        }

                        // PY resources
                        for (int i = 1; i <= 12; ++i) {
                            loadImageFromResource("/py/spr_" + i + ".png");
                        }

                        // SquashCourtScene resources
                        loadImageFromResource("/gym/squash_court/floor.png");

                        // SquashScene resources
                        loadImageFromResource("/gym/squash_game/floor.png");

                        // CourtHallwayScene resources
                        loadImageFromResource("/gym/court_hallway/floor.png");

                        // LibraryScene resources
                        loadImageFromResource("/colby/library/floor.png");
                        loadImageFromResource("/colby/library/chair.png");
                        loadImageFromResource("/colby/library/table.png");

                        loadImageFromResource("/colby/library/bookshelf.png");

                        loadImageFromResource("/colby/library/com.png");
                        loadImageFromResource("/colby/library/com_chair.png");
                        loadImageFromResource("/colby/library/com_table.png");

                        loadImageFromResource("/colby/library/office.png");
                        loadImageFromResource("/colby/library/fountain.png");

                        loadImageFromResource("/colby/library/sofa_big.png");
                        loadImageFromResource("/colby/library/sofa_table.png");

                        // ColbyHallwayScene resources
                        loadImageFromResource("/colby/colby_hallway/floor.png");

                        // BusinessOfficeScene resources
                        loadImageFromResource("/colby/business_office/floor.png");
                        loadImageFromResource("/colby/business_office/table.png");
                        loadImageFromResource("/colby/business_office/chair_in.png");
                        loadImageFromResource("/colby/business_office/chair_out.png");
                        loadImageFromResource("/colby/business_office/shelf.png");
                        loadImageFromResource("/colby/business_office/printer.png");

                        // Load our very sick looking fonts!
                        ResourceManager.loadDelay("/VT323-Regular.ttf",
                                SceneManager.frame().getRuntime().getFontDecoder());

                        // Load game session, see Portals for game state stuff
                        ResourceManager.loadOrDefault("./save.dat", ExternalResourceResolver.INSTANCE,
                                SerializableDecoder.forType(GameData.class), GameData::new);

                        loaded = LoadState.AWAIT_FUTURE;
                    } catch (DecodeException ex) {
                        ex.printStackTrace();
                        loaded = LoadState.NO_RES;
                        return;
                    }
                });
                break;
        }

        return true;
    }

    private void loadImageFromResource(final String path) {
        ResourceManager.loadDelay(path, SceneManager.frame().getRuntime().getTextureDecoder());
    }

    private void loadMusicFromResource(final String path) {
        ResourceManager.load(path, AudioDecoder.INSTANCE);
    }
}
