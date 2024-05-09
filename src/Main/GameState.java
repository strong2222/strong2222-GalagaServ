import Actors.Bullet;
import Actors.Enemy;
import Actors.Player;
import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    public List<Enemy> enemies;
    public List<Bullet> bullets;
    public List<Player> players;

    public GameState(List<Enemy> enemies, List<Bullet> bullets, List<Player> players) {
        this.enemies = enemies;
        this.bullets = bullets;
        this.players = players;
    }
}
