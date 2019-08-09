package models.battleFields;

import models.battleFields.interfaces.Battlefield;
import models.cards.interfaces.Card;
import models.players.Beginner;
import models.players.interfaces.Player;

import static common.ConstantMessages.*;

public class BattleFieldImpl implements Battlefield {
    private static final int INCREASE_PLAYER_HEALTH = 40;
    private static final int INCREASE_CARD_DAMAGE = 30;

    @Override
    public void fight(Player attackPlayer, Player enemyPlayer) {
        if (attackPlayer.isDead() || enemyPlayer.isDead()) {
            throw new IllegalArgumentException(PLAYER_IS_DEAD);
        }

        preFightPreparation(attackPlayer);
        preFightPreparation(enemyPlayer);

        getHealthPointsFromDeck(attackPlayer);
        getHealthPointsFromDeck(enemyPlayer);

        processingFight(attackPlayer, enemyPlayer);
    }

    private void processingFight(Player attackPlayer, Player enemyPlayer) {
        while (true) {
            int attackPoints = attackPlayer
                    .getCardRepository()
                    .getCards()
                    .stream()
                    .mapToInt(Card::getDamagePoints)
                    .sum();

            enemyPlayer.takeDamage(attackPoints);

            if (enemyPlayer.isDead()) {
                return;
            }

            int enemyPoints = enemyPlayer
                    .getCardRepository()
                    .getCards()
                    .stream()
                    .mapToInt(Card::getDamagePoints)
                    .sum();

            attackPlayer.takeDamage(enemyPoints);

            if (attackPlayer.isDead()) {
                return;
            }
        }
    }

    private void getHealthPointsFromDeck(Player player) {
        // So, we have to take the sum of all cards in our collection!
        int healthPoints = player
                .getCardRepository()
                .getCards()
                .stream()
                .mapToInt(Card::getHealthPoints)
                .sum();

        player.setHealth(player.getHealth() + healthPoints);
    }

    private void preFightPreparation(Player player) {
        if (!Beginner.class.getSimpleName().equals(player.getClass().getSimpleName())) {
            return;
        }

        player.setHealth(player.getHealth() + INCREASE_PLAYER_HEALTH);
        player
                .getCardRepository()
                .getCards()
                .forEach(c -> c.setDamagePoints(c.getDamagePoints() + INCREASE_CARD_DAMAGE));
    }
}
