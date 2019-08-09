package core;

import core.interfaces.ManagerController;
import models.battleFields.BattleFieldImpl;
import models.battleFields.interfaces.Battlefield;
import models.cards.MagicCard;
import models.cards.TrapCard;
import models.cards.interfaces.Card;
import models.players.Advanced;
import models.players.Beginner;
import models.players.interfaces.Player;
import repositories.CardRepositoryImpl;
import repositories.PlayerRepositoryImpl;
import repositories.interfaces.CardRepository;
import repositories.interfaces.PlayerRepository;

import static common.ConstantMessages.*;

public class ManagerControllerImpl implements ManagerController {
    private static final String CARD_TYPE_MAGIC = "Magic";
    private static final String CARD_TYPE_TRAP = "Trap";
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;
    private Battlefield battlefield;

    public ManagerControllerImpl() {
        this.playerRepository = new PlayerRepositoryImpl();
        this.cardRepository = new CardRepositoryImpl();
        this.battlefield = new BattleFieldImpl();
    }

    @Override
    public String addPlayer(String type, String username) {
        Player player = null;

       if (Beginner.class.getSimpleName().equals(type)) {
           player = new Beginner(new CardRepositoryImpl(), username);
       } else if (Advanced.class.getSimpleName().equals(type)) {
           player = new Advanced(new CardRepositoryImpl(), username);
       }

       this.playerRepository.add(player);
        return String.format(SUCCESSFULLY_ADDED_PLAYER, type, username);
    }

    @Override
    public String addCard(String type, String name) {
        Card card = null;

        if (CARD_TYPE_MAGIC.equals(type)) {
            card = new MagicCard(name);
        } else if (CARD_TYPE_TRAP.equals(type)) {
            card = new TrapCard(name);
        }

        this.cardRepository.add(card);
        return String.format(SUCCESSFULLY_ADDED_CARD, type, name);
    }

    @Override
    public String addPlayerCard(String username, String cardName) {
        Card card = this.cardRepository.find(cardName);
        Player player = this.playerRepository.find(username);
        player.getCardRepository().add(card);
        return String.format(SUCCESSFULLY_ADDED_PLAYER_WITH_CARDS, cardName, username);
    }

    @Override
    public String fight(String attackUser, String enemyUser) {
        Player attackPlayer = this.playerRepository.find(attackUser);
        Player enemyPlayer = this.playerRepository.find(enemyUser);
        this.battlefield.fight(attackPlayer, enemyPlayer);

        return String.format(FIGHT_INFO, attackPlayer.getHealth(), enemyPlayer.getHealth());
    }

    @Override
    public String report() {
        StringBuilder sb = new StringBuilder();
        for (Player player : this.playerRepository.getPlayers()) {
            sb.append(String.format(PLAYER_REPORT_INFO,
                    player.getUsername(),
                    player.getHealth(),
                    player.getCardRepository().getCount()))
            .append(System.lineSeparator());
            player.getCardRepository()
                    .getCards().forEach(c ->
                    sb.append(String.format(CARD_REPORT_INFO, c.getName(), c.getDamagePoints()))
                            .append(System.lineSeparator()));
            sb.append(DEFAULT_REPORT_SEPARATOR).append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}
