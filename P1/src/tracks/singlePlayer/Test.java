package tracks.singlePlayer;

import java.util.ArrayList;
import java.util.Random;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;
import tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL.BoulderDashMaze;
import tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL.MapType;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test {

    public static void main(String[] args) {

		String myCamelController = "tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL.myAgent_camel";

		String dijkstraController = "tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL.AgenteDijkstra";
		String AStarController = "tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL.AgenteAStar";
		String RTAStarController = "tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL.AgenteRTAStar";
		String LRTAStarController = "tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL.AgenteLRTAStar";
		String competicionController = "tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL.AgenteCompeticion";

		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();

		// // Fragmento de codigo para ejecutar todos los controladores en los mapas principales
		// ArrayList<String> agents = new ArrayList<>();
		// ArrayList<MapType> maps = new ArrayList<>();

		// // BoulderDashMaze.Simple o BoulderDashMaze.Extended
		// BoulderDashMaze gameType = BoulderDashMaze.Extended;

		// // Mapas a emplear
		// maps.add(MapType.Small);
		// maps.add(MapType.Medium);
		// maps.add(MapType.Large);

		// // // Controladores a evaluar
		// if (gameType == BoulderDashMaze.Simple) {
		// 	agents.add(dijkstraController);
		// 	agents.add(AStarController);
		// }
		// agents.add(RTAStarController);
		// agents.add(LRTAStarController);

		// for (String agent : agents) {
		// 	for (MapType map : maps) {
		// 		System.out.println(agent + "\n" + map.toString());
		// 		int gameIdx = gameType.getId();
		// 		int levelIdx = map.getId();
		// 		String gameName = games[gameIdx][1];
		// 		String game = games[gameIdx][0];
		// 		String level = game.replace(gameName, gameName + "_lvl" + levelIdx);
		// 		ArcadeMachine.runOneGame(game, level, visuals, agent, null, seed, 0);
		// 	}
		// }

		// Ejecutar un juego individual
		int gameIdx = 123;
		int levelIdx = 1;
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		String level = game.replace(gameName, gameName + "_lvl" + levelIdx);
		try {
			ArcadeMachine.runOneGame(game, level, visuals, RTAStarController, null, seed, 0);
			Thread.sleep(2000);
		} catch (Exception e) {
			
		}

		System.exit(0);

    }
}
