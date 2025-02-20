import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;

public class Game {

    private ArrayList<Account> acconuts_list;
    private Grid Map;
    private JPanel matrixPanel;
    private JFrame gameFrame;
    private final Object lock = new Object();
    private String chosenCommand = null;


    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Account acc : acconuts_list) {
            sb.append(
                    acc.getAccountData().getLoginData().getMail() + "\n" + acc.getAccountData().getLoginData().getPsw()
                            + "\n" + acc.getAccountData().getName() + "\n" + acc.getAccountData().getCountry() + "\n"
                            + acc.getGamesPlayed() + "\n");
            for (String game : acc.getAccountData().getFavoriteGames()) {
                sb.append(game + "\n");
            }
            sb.append("\n");
            for (Character player : acc.getAccountCharacters()) {
                sb.append(player.current_health + " " + player.max_health + " " + player.current_mana + " "
                        + player.max_mana + " " + player.fire_imune + " " + player.ice_imune + " " + player.earth_imune
                        + " " + player.character_name + " " + player.character_exp + " " + player.character_level + " "
                        + player.character_strength + " " + player.character_charisma + " " + player.character_dexterity
                        + " " + player.main_atribute);
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public CellEntityType show_options(String line, CellEntityType type)
            throws InvalidCommandException, ImpossibleMove, QuitGame, Game_Over {

        System.out.println("Mutari valide:");

        boolean ok_right = true, ok_left = true, ok_up = true, ok_down = true;
        if (Map.get_current_Cell().getPozY() + 1 == Map.getNrCol()) {
            ok_right = false;
        } else {
            System.out.println("Right");
        }
        if (Map.get_current_Cell().getPozY() == 0) {
            ok_left = false;
        } else {
            System.out.println("Left");
        }
        if (Map.get_current_Cell().getPozX() == 0) {
            ok_up = false;
        } else {
            System.out.println("Up");
        }
        if (Map.get_current_Cell().getPozX() + 1 == Map.getNrLin()) {
            ok_down = false;
        } else {
            System.out.println("Down");
        }


        if (line.isEmpty()) {
            throw new InvalidCommandException();
        }

        char command = line.charAt(0);

        if (command == 'q') {
            throw new QuitGame();
        }

        if (command != 'w' && command != 'a' && command != 's' && command != 'd')
            throw new InvalidCommandException();
        else if (command == 'w') {
            if (ok_up) {
                return Map.goNorth(type);
            } else {
                throw new ImpossibleMove();
            }
        } else if (command == 'a') {
            if (ok_left) {
                return Map.goWest(type);
            } else {
                throw new ImpossibleMove();
            }
        } else if (command == 's') {
            if (ok_down) {
                return Map.goSouth(type);
            } else {
                throw new ImpossibleMove();
            }
        } else {
            if (ok_right) {
                return Map.goEast(type);
            } else {
                throw new ImpossibleMove();
            }
        }
    }

    public void show_map() {
        SwingUtilities.invokeLater(() -> {
            gameFrame = new JFrame("Map Display");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setSize(500, 500);
            gameFrame.setLayout(new BorderLayout());

            matrixPanel = new JPanel();
            matrixPanel.setLayout(new GridLayout(Map.getNrLin(), Map.getNrCol()));
            gameFrame.add(matrixPanel, BorderLayout.CENTER);

            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new GridLayout(2, 3));
            JPanel quitPanel = new JPanel();
            quitPanel.setLayout(new GridLayout(1, 3));

            JButton upButton = new JButton("Up");
            JButton downButton = new JButton("Down");
            JButton leftButton = new JButton("Left");
            JButton rightButton = new JButton("Right");
            JButton quitButton = new JButton("Quit");

            controlPanel.add(new JLabel());
            controlPanel.add(upButton);
            controlPanel.add(new JLabel());
            controlPanel.add(leftButton);
            controlPanel.add(downButton);
            controlPanel.add(rightButton);
            quitPanel.add(quitButton);
            quitPanel.add(new JLabel());
            quitPanel.add(new JLabel());

            upButton.addActionListener(e -> handleDirection("w"));
            downButton.addActionListener(e -> handleDirection("s"));
            leftButton.addActionListener(e -> handleDirection("a"));
            rightButton.addActionListener(e -> handleDirection("d"));
            quitButton.addActionListener(e -> handleDirection("q"));

            gameFrame.add(controlPanel, BorderLayout.SOUTH);
            gameFrame.add(quitPanel, BorderLayout.NORTH);

            updateMap();

            gameFrame.setVisible(true);
        });
    }

    public void handleDirection(String command) {
        synchronized (lock) {
            chosenCommand = command;
            lock.notify();
        }
    }

    public void updateMap() {
        matrixPanel.removeAll();
        for (int i = 0; i < Map.getNrLin(); i++) {
            for (int j = 0; j < Map.getNrCol(); j++) {
                JPanel cellPanel = new JPanel();
                Cell cell = Map.getCell(i, j);

                switch (cell.getType()) {
                    case VOID:
                        cellPanel.setBackground(cell.getVisited() == 1 ? Color.GRAY : Color.WHITE);
                        break;
                    case SANCTUARY:
                        cellPanel.setBackground(cell.getVisited() == 1 ? Color.CYAN : Color.WHITE);
                        break;
                    case ENEMY:
                        cellPanel.setBackground(cell.getVisited() == 1 ? Color.RED : Color.WHITE);
                        break;
                    case PORTAL:
                        cellPanel.setBackground(cell.getVisited() == 1 ? Color.MAGENTA : Color.WHITE);
                        break;
                    case PLAYER:
                        cellPanel.setBackground(cell.getVisited() == 1 ? Color.GREEN : Color.WHITE);
                        break;
                }

                cellPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                matrixPanel.add(cellPanel);
            }
        }

        matrixPanel.revalidate();
        matrixPanel.repaint();
    }

    public void closeGameWindow() {
        if (gameFrame != null) {
            gameFrame.dispose();
        }
    }

    public void remove_map() {
        for (int i = 0; i < Map.getNrLin(); i++) {
            Map.removeAll(Map.get(i));
        }
        Map.setInstance(null);
    }

    public void next_level() {

        Random random = new Random();
        int lines = 3 + random.nextInt(8);
        int columns = 3 + random.nextInt(8);
        int start_x = random.nextInt(lines);
        int start_y = random.nextInt(columns);

        Map = Grid.construct_grid(lines, columns, Map.get_current_Character(),
                new Cell(start_x, start_y, CellEntityType.PLAYER, 1));
    }

    public void init_void() {

        for (int i = 0; i < Map.getNrLin(); i++)
            for (int j = 0; j < Map.getNrCol(); j++)
                if (Map.get(i).get(j).getType() != CellEntityType.PLAYER
                        && Map.get(i).get(j).getType() != CellEntityType.VOID) {
                    Map.get(i).get(j).setType(CellEntityType.VOID);
                }
    }

    public int createLoginUI(ArrayList<Account> accounts_list) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger loggedInIndex = new AtomicInteger(-1);

        JFrame frame = new JFrame("Pagina de Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 200);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        frame.add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Parola:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        frame.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                boolean emailValid = false;
                boolean passwordValid = false;

                for (int i = 0; i < accounts_list.size(); i++) {
                    Account acc = accounts_list.get(i);
                    if (email.equals(acc.getAccountData().getLoginData().getMail())) {
                        emailValid = true;
                        if (password.equals(acc.getAccountData().getLoginData().getPsw())) {
                            passwordValid = true;
                            loggedInIndex.set(i);
                        }
                        break;
                    }
                }

                if (!emailValid) {
                    JOptionPane.showMessageDialog(frame, "Email invalid!", "Eroare", JOptionPane.ERROR_MESSAGE);
                } else if (!passwordValid) {
                    JOptionPane.showMessageDialog(frame, "Parola gresita!", "Eroare", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Logat cu succes!");
                    frame.dispose();
                    latch.countDown();
                }
            }
        });

        frame.setVisible(true);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return loggedInIndex.get();
    }

    public int createCharacterSelectionUI(Account log_acc) {
        JFrame frame = new JFrame("Selecteaza personajul");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ArrayList<Character> characters = log_acc.getAccountCharacters();
        AtomicInteger selectedIndexAtomic = new AtomicInteger(-1);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(characters.size(), 2, 10, 10));

        for (int i = 0; i < characters.size(); i++) {
            Character character = characters.get(i);
            int index = i;
            JButton button = new JButton(character.character_name);
            button.addActionListener(e -> {
                selectedIndexAtomic.set(index);
                JOptionPane.showMessageDialog(frame, "Ai ales personajul: " + character.character_name);
                frame.dispose();
            });
            panel.add(button);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(scrollPane, gbc);

        frame.setVisible(true);

        while (selectedIndexAtomic.get() == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return selectedIndexAtomic.get();
    }


    public void run() {
        acconuts_list = JsonInput.deserializeAccounts();
        int idx = createLoginUI(acconuts_list);
        Account log_acc = acconuts_list.get(idx);

        Scanner scanner = new Scanner(System.in);

        int idx_pers = createCharacterSelectionUI(log_acc);

        while (true) {

            System.out.println("Alege modul jocului: Play(p) sau Test(t)");
            String line = scanner.nextLine();

            if (line.isEmpty()) {
                System.out.println("Introdu o comanda");
                continue;
            }

            char command = line.charAt(0);

            if (command == 'p') {

                Random random = new Random();
                int lines = 3 + random.nextInt(8);
                int columns = 3 + random.nextInt(8);
                int start_x = random.nextInt(lines);
                int start_y = random.nextInt(columns);

                Map = Grid.construct_grid(lines, columns, log_acc.getAccountCharacters().get(idx_pers),
                        new Cell(start_x, start_y, CellEntityType.PLAYER, 1));
                break;
            } else if (command == 't') {

                Map = Grid.construct_grid(5, 5, log_acc.getAccountCharacters().get(idx_pers),
                        new Cell(0, 0, CellEntityType.PLAYER, 1));
                init_void();
                Map.getCell(0, 3).setType(CellEntityType.SANCTUARY);
                Map.getCell(1, 3).setType(CellEntityType.SANCTUARY);
                Map.getCell(2, 0).setType(CellEntityType.SANCTUARY);
                Map.getCell(4, 3).setType(CellEntityType.SANCTUARY);
                Map.getCell(3, 4).setType(CellEntityType.ENEMY);
                Map.getCell(4, 4).setType(CellEntityType.PORTAL);

                break;
            } else {
                System.out.println("Mod invalid");
            }

        }

        CellEntityType type = CellEntityType.VOID;

        show_map();

        while (true) {
            try {
                synchronized (lock) {
                    while (chosenCommand == null) {
                        try {
                            lock.wait();
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                type = show_options(chosenCommand, type);
                chosenCommand = null;

                updateMap();

                if (type == null) {
                    closeGameWindow();
                    remove_map();
                    next_level();
                    show_map();
                    type = CellEntityType.VOID;
                    System.out.println("Ai gasit portalul!");
                }
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage() + "\n" + "Introdu o comanda valida");
                chosenCommand = null;
            } catch (ImpossibleMove e) {
                System.out.println(e.getMessage() + "\n" + "Introdu o mutare valida");
                chosenCommand = null;
            } catch (QuitGame e) {
                System.out.println("Exited the game");
                closeGameWindow();
                break;
            } catch (Game_Over e) {
                System.out.println("Game Over!");
                closeGameWindow();
                JFrame gameOverFrame = new JFrame("Game Over");
                gameOverFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                gameOverFrame.setSize(400, 300);
                gameOverFrame.setLayout(new BorderLayout());

                JLabel titleLabel = new JLabel("Game Over!", JLabel.CENTER);
                titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
                gameOverFrame.add(titleLabel, BorderLayout.NORTH);

                JPanel statusPanel = new JPanel();
                statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
                JScrollPane scrollPane = new JScrollPane(statusPanel);
                gameOverFrame.add(scrollPane, BorderLayout.CENTER);

                for (String line : Map.character_status().split("\n")) {
                    JLabel statusLabel = new JLabel(line);
                    statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    statusPanel.add(statusLabel);
                }

                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(ev -> gameOverFrame.dispose());
                gameOverFrame.add(closeButton, BorderLayout.SOUTH);

                gameOverFrame.setVisible(true);
                break;
            }
        }

    }
}

enum CellEntityType {
    PLAYER, VOID, ENEMY, SANCTUARY, PORTAL;
}

class Game_Over extends Exception {
    public Game_Over() {
        super();
    }
}

class InvalidCommandException extends Exception {
    public InvalidCommandException() {
        super("Comanda invalida");
    }
}

class ImpossibleMove extends Exception {
    public ImpossibleMove() {
        super("Mutare invalida");
    }
}

class QuitGame extends Exception {
    public QuitGame() {
        super();
    }
}

class Grid extends ArrayList<ArrayList<Cell>> {
    private int nr_lin, nr_col;
    private Character current_Character;
    private Cell current_Cell;
    private static Grid instance = null;

    private Grid(int nr_lin, int nr_col, Character current_Character, Cell current_Cell) {
        this.nr_lin = nr_lin;
        this.nr_col = nr_col;
        this.current_Character = current_Character;
        this.current_Cell = current_Cell;

        for (int i = 0; i < nr_lin; i++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0; j < nr_col; j++) {
                if (i == current_Cell.getPozX() && j == current_Cell.getPozY())
                    row.add(current_Cell);
                else {
                    row.add(new Cell(i, j, CellEntityType.VOID, 0));
                }
            }
            add(row);
        }

        Random random = new Random();

        int min = Math.min(nr_lin, nr_col);
        int nr_sanct, nr_enemy, nr_portal = 1;

        while (true) {
            nr_sanct = 2 + random.nextInt(min);
            nr_enemy = 4 + random.nextInt(min);
            if (nr_enemy + nr_sanct <= nr_lin * nr_col - 2)
                break;
        }

        while (true) {
            if (nr_sanct == 0) {
                break;
            } else {
                int poz_x = random.nextInt(nr_lin);
                int poz_y = random.nextInt(nr_col);
                if (getCell(poz_x, poz_y).getType() == CellEntityType.VOID) {
                    nr_sanct--;
                    getCell(poz_x, poz_y).setType(CellEntityType.SANCTUARY);
                }
            }
        }

        while (true) {
            if (nr_enemy == 0) {
                break;
            } else {
                int poz_x = random.nextInt(nr_lin);
                int poz_y = random.nextInt(nr_col);
                if (getCell(poz_x, poz_y).getType() == CellEntityType.VOID) {
                    nr_enemy--;
                    getCell(poz_x, poz_y).setType(CellEntityType.ENEMY);
                }
            }
        }

        while (true) {
            if (nr_portal == 0) {
                break;
            } else {
                int poz_x = random.nextInt(nr_lin);
                int poz_y = random.nextInt(nr_col);
                if (getCell(poz_x, poz_y).getType() == CellEntityType.VOID) {
                    nr_portal--;
                    getCell(poz_x, poz_y).setType(CellEntityType.PORTAL);
                }
            }
        }
    }

    public static Grid construct_grid(int nr_lin, int nr_col, Character current_Character, Cell current_Cell) {
        if(instance == null) {
            instance = new Grid(nr_lin, nr_col, current_Character, current_Cell);
        }
        return instance;
    }

    public String character_status() {
        StringBuilder sb = new StringBuilder();
        sb.append(current_Character.character_name + "\n");
        sb.append("Health: " + current_Character.current_health + "\n");
        sb.append("Mana: " + current_Character.current_mana + "\n");
        sb.append("Exp: " + current_Character.character_exp + "\n");
        sb.append("Level: " + current_Character.character_level + "\n");
        sb.append("Enemys killed: " + current_Character.kill_count + "\n");
        sb.append("Imunity to: ");
        if (current_Character.earth_imune) {
            sb.append("Earth ");
        }
        if (current_Character.fire_imune) {
            sb.append("Fire ");
        }
        if (current_Character.ice_imune) {
            sb.append("Ice ");
        }
        return sb.toString();
    }

    public String enemy_status(Entity enemy) {
        StringBuilder sb = new StringBuilder();
        sb.append("Inamic\n");
        sb.append("Health: " + enemy.current_health + "\n");
        sb.append("Mana: " + enemy.current_mana + "\n");
        sb.append("Imunity to: ");
        if (enemy.earth_imune) {
            sb.append("Earth ");
        }
        if (enemy.fire_imune) {
            sb.append("Fire ");
        }
        if (enemy.ice_imune) {
            sb.append("Ice ");
        }
        return sb.toString();
    }

    public String show_spells() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Spell sp : get_current_Character().spell_list) {
            sb.append(i + "\n" + sp.toString());
            i++;
        }
        return sb.toString();
    }

    public String show_enemy_spells(Entity enemy) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Spell sp : enemy.spell_list) {
            sb.append(i + "\n" + sp.toString());
            i++;
        }
        return sb.toString();
    }

    public boolean fight() {
        JFrame fightFrame = new JFrame("Fight!");
        fightFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fightFrame.setSize(600, 400);
        fightFrame.setLayout(new BorderLayout());

        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        fightFrame.add(playerPanel, BorderLayout.WEST);

        JPanel enemyPanel = new JPanel();
        enemyPanel.setLayout(new BoxLayout(enemyPanel, BoxLayout.Y_AXIS));
        fightFrame.add(enemyPanel, BorderLayout.EAST);

        JPanel actionPanel = new JPanel();
        JButton normalAttackButton = new JButton("Normal Attack");
        JButton castSpellButton = new JButton("Cast Spell");
        actionPanel.add(normalAttackButton);
        actionPanel.add(castSpellButton);
        fightFrame.add(actionPanel, BorderLayout.CENTER);

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        fightFrame.add(scrollPane, BorderLayout.SOUTH);

        Random random = new Random();
        boolean imune1, imune2, imune3;
        ArrayList<Spell> aux_spells = new ArrayList<>();

        while (true) {
            imune1 = random.nextBoolean();
            imune2 = random.nextBoolean();
            imune3 = random.nextBoolean();
            if (((imune1 && imune2 && imune3) == false) && (imune1 || imune2 || imune3)) {
                break;
            }
        }

        Entity enemy = new Enemy(50 + random.nextInt(50), 100 + random.nextInt(50), 50 + random.nextInt(50),
                70 + random.nextInt(50), imune1, imune2, imune3, new ArrayList<>());

        logArea.append("Lupta a inceput\n");

        Runnable updateStatus = () -> {
            playerPanel.removeAll();
            enemyPanel.removeAll();

            for (String stat : character_status().split("\n")) {
                playerPanel.add(new JLabel(stat));
            }

            for (String stat : enemy_status(enemy).split("\n")) {
                enemyPanel.add(new JLabel(stat));
            }

            playerPanel.revalidate();
            playerPanel.repaint();
            enemyPanel.revalidate();
            enemyPanel.repaint();
        };

        updateStatus.run();

        normalAttackButton.addActionListener(e -> {
            if (enemy.current_health > 0 && get_current_Character().current_health > 0) {
                logArea.append("Player-ul foloseste un atac normal\n");
                enemy.normal_attack();

                if (enemy.current_health <= 0) {
                    System.out.println("Inamic infrant!\n");
                    get_current_Character().spell_list.addAll(aux_spells);
                    get_current_Character().kill_count++;
                    fightFrame.dispose();
                } else {
                    enemyTurn(logArea, updateStatus, enemy, fightFrame);
                }
            }
            updateStatus.run();
        });

        castSpellButton.addActionListener(e -> {
            if (get_current_Character().spell_list.isEmpty()) {
                logArea.append("Nu mai ai spell-uri disponibile.\n");
                return;
            }

            JFrame spellFrame = new JFrame("Selecteaza un spell");
            spellFrame.setSize(300, 200);
            spellFrame.setLayout(new GridLayout(get_current_Character().spell_list.size(), 1));

            for (int i = 0; i < get_current_Character().spell_list.size(); i++) {
                int spellIndex = i;
                Spell spell = get_current_Character().spell_list.get(i);
                JButton spellButton = new JButton(spell.toString().replace("\n", " "));
                spellButton.addActionListener(ev -> {
                    if (get_current_Character().current_mana < spell.spell_mana) {
                        logArea.append("Nu ai suficienta mana pentru acest spell\n");
                    } else {
                        logArea.append("Player-ul folostete spell-ul " + spell.spell_type + ".\n");
                        enemy.accept(spell);
                        get_current_Character().current_mana -= spell.spell_mana;
                        aux_spells.add(spell);
                        get_current_Character().spell_list.remove(spellIndex);

                        if (enemy.current_health <= 0) {
                            System.out.println("Inamic infrant!\n");
                            get_current_Character().spell_list.addAll(aux_spells);
                            get_current_Character().kill_count++;
                            spellFrame.dispose();
                            fightFrame.dispose();
                        } else {
                            enemyTurn(logArea, updateStatus, enemy, fightFrame);
                        }
                    }
                    spellFrame.dispose();
                    updateStatus.run();
                });
                spellFrame.add(spellButton);
            }

            spellFrame.setVisible(true);
        });

        fightFrame.setVisible(true);

        while (fightFrame.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }

        return get_current_Character().current_health > 0;
    }

    public void enemyTurn(JTextArea logArea, Runnable updateStatus, Entity enemy, JFrame fightFrame) {
        Random random = new Random();

        if (enemy.spell_list.isEmpty()) {
            logArea.append("Inamicul foloseste un atac normal\n");
            get_current_Character().normal_attack();
        } else {
            int enemyAttackIndex = random.nextInt(enemy.spell_list.size());
            Spell spell = enemy.spell_list.get(enemyAttackIndex);

            if (enemy.current_mana < spell.spell_mana) {
                logArea.append("Inamicul nu are suficienta mana pentru spell-ul " + spell.spell_type + ".\n");
                get_current_Character().normal_attack();
            } else {
                logArea.append("Inamicul foloseste spell-ul " + spell.spell_type + ".\n");
                get_current_Character().accept(spell);
                enemy.current_mana -= spell.spell_mana;
                enemy.spell_list.remove(enemyAttackIndex);
            }
        }

        if (get_current_Character().current_health <= 0) {
            System.out.println("Player infrant!\n");
            fightFrame.dispose();
            return;
        }

        updateStatus.run();
    }



    public CellEntityType goNorth(CellEntityType type) throws Game_Over {
        get_current_Cell().setType(type);
        set_current_Cell(getCell(get_current_Cell().getPozX() - 1, get_current_Cell().getPozY()));
        CellEntityType new_type = get_current_Cell().getType();
        if (new_type == CellEntityType.SANCTUARY && get_current_Cell().getVisited() == 0) {
            Random random = new Random();
            get_current_Character().regenerate_health(get_current_Character().current_health
                    + random.nextInt(1 + get_current_Character().max_health - get_current_Character().current_health));
            get_current_Character().regenerate_mana(get_current_Character().current_mana
                    + random.nextInt(1 + get_current_Character().max_mana - get_current_Character().current_mana));
            System.out.println("Ai gasit un sanctuar\n" + character_status());
        }
        if (new_type == CellEntityType.PORTAL) {
            get_current_Character().regenerate_health(get_current_Character().max_health);
            get_current_Character().regenerate_mana(get_current_Character().max_mana);
            get_current_Character().character_level++;
            get_current_Character().character_exp = get_current_Character().character_exp
                    + 5 * get_current_Character().character_level;
            return null;
        }
        if (new_type == CellEntityType.ENEMY && get_current_Cell().getVisited() == 0) {
            Random random = new Random();
            if (fight() == false) {
                throw new Game_Over();
            } else {
                get_current_Character().regenerate_health(get_current_Character().current_health
                        + random.nextInt(
                                1 + get_current_Character().max_health - get_current_Character().current_health));
                get_current_Character().regenerate_mana(get_current_Character().current_mana
                        + random.nextInt(1 + get_current_Character().max_mana - get_current_Character().current_mana));
            }
        }
        get_current_Cell().setType(CellEntityType.PLAYER);
        get_current_Cell().setVisited(1);
        return new_type;
    }

    public CellEntityType goSouth(CellEntityType type) throws Game_Over {
        get_current_Cell().setType(type);
        set_current_Cell(getCell(get_current_Cell().getPozX() + 1, get_current_Cell().getPozY()));
        CellEntityType new_type = get_current_Cell().getType();
        if (new_type == CellEntityType.SANCTUARY && get_current_Cell().getVisited() == 0) {
            Random random = new Random();
            get_current_Character().regenerate_health(get_current_Character().current_health
                    + random.nextInt(1 + get_current_Character().max_health - get_current_Character().current_health));
            get_current_Character().regenerate_mana(get_current_Character().current_mana
                    + random.nextInt(1 + get_current_Character().max_mana - get_current_Character().current_mana));
            System.out.println("Ai gasit un sanctuar\n" + character_status());
        }
        if (new_type == CellEntityType.PORTAL) {
            get_current_Character().regenerate_health(get_current_Character().max_health);
            get_current_Character().regenerate_mana(get_current_Character().max_mana);
            get_current_Character().character_level++;
            get_current_Character().character_exp = get_current_Character().character_exp
                    + 5 * get_current_Character().character_level;
            return null;
        }
        if (new_type == CellEntityType.ENEMY && get_current_Cell().getVisited() == 0) {
            Random random = new Random();
            if (fight() == false) {
                throw new Game_Over();
            } else {
                get_current_Character().regenerate_health(get_current_Character().current_health
                        + random.nextInt(
                                1 + get_current_Character().max_health - get_current_Character().current_health));
                get_current_Character().regenerate_mana(get_current_Character().current_mana
                        + random.nextInt(1 + get_current_Character().max_mana - get_current_Character().current_mana));
            }
        }
        get_current_Cell().setType(CellEntityType.PLAYER);
        get_current_Cell().setVisited(1);
        return new_type;
    }

    public CellEntityType goWest(CellEntityType type) throws Game_Over {
        get_current_Cell().setType(type);
        set_current_Cell(getCell(get_current_Cell().getPozX(), get_current_Cell().getPozY() - 1));
        CellEntityType new_type = get_current_Cell().getType();
        if (new_type == CellEntityType.SANCTUARY && get_current_Cell().getVisited() == 0) {
            Random random = new Random();
            get_current_Character().regenerate_health(get_current_Character().current_health
                    + random.nextInt(1 + get_current_Character().max_health - get_current_Character().current_health));
            get_current_Character().regenerate_mana(get_current_Character().current_mana
                    + random.nextInt(1 + get_current_Character().max_mana - get_current_Character().current_mana));
            System.out.println("Ai gasit un sanctuar\n" + character_status());
        }
        if (new_type == CellEntityType.PORTAL) {
            get_current_Character().regenerate_health(get_current_Character().max_health);
            get_current_Character().regenerate_mana(get_current_Character().max_mana);
            get_current_Character().character_level++;
            get_current_Character().character_exp = get_current_Character().character_exp
                    + 5 * get_current_Character().character_level;
            return null;
        }
        if (new_type == CellEntityType.ENEMY && get_current_Cell().getVisited() == 0) {
            Random random = new Random();
            if (fight() == false) {
                throw new Game_Over();
            } else {
                get_current_Character().regenerate_health(get_current_Character().current_health
                        + random.nextInt(
                                1 + get_current_Character().max_health - get_current_Character().current_health));
                get_current_Character().regenerate_mana(get_current_Character().current_mana
                        + random.nextInt(1 + get_current_Character().max_mana - get_current_Character().current_mana));
            }
        }
        get_current_Cell().setType(CellEntityType.PLAYER);
        get_current_Cell().setVisited(1);
        return new_type;
    }

    public CellEntityType goEast(CellEntityType type) throws Game_Over {
        get_current_Cell().setType(type);
        set_current_Cell(getCell(get_current_Cell().getPozX(), get_current_Cell().getPozY() + 1));
        CellEntityType new_type = get_current_Cell().getType();
        if (new_type == CellEntityType.SANCTUARY && get_current_Cell().getVisited() == 0) {
            Random random = new Random();
            get_current_Character().regenerate_health(get_current_Character().current_health
                    + random.nextInt(1 + get_current_Character().max_health - get_current_Character().current_health));
            get_current_Character().regenerate_mana(get_current_Character().current_mana
                    + random.nextInt(1 + get_current_Character().max_mana - get_current_Character().current_mana));
            System.out.println("Ai gasit un sanctuar\n" + character_status());
        }
        if (new_type == CellEntityType.PORTAL) {
            get_current_Character().regenerate_health(get_current_Character().max_health);
            get_current_Character().regenerate_mana(get_current_Character().max_mana);
            get_current_Character().character_level++;
            get_current_Character().character_exp = get_current_Character().character_exp
                    + 5 * get_current_Character().character_level;
            return null;
        }
        if (new_type == CellEntityType.ENEMY && get_current_Cell().getVisited() == 0) {
            Random random = new Random();
            if (fight() == false) {
                throw new Game_Over();
            } else {
                get_current_Character().regenerate_health(get_current_Character().current_health
                        + random.nextInt(
                                1 + get_current_Character().max_health - get_current_Character().current_health));
                get_current_Character().regenerate_mana(get_current_Character().current_mana
                        + random.nextInt(1 + get_current_Character().max_mana - get_current_Character().current_mana));
            }
        }
        get_current_Cell().setType(CellEntityType.PLAYER);
        get_current_Cell().setVisited(1);
        return new_type;
    }

    public int getNrLin() {
        return nr_lin;
    }

    public void setNrLin(int nr_lin) {
        this.nr_lin = nr_lin;
    }

    public int getNrCol() {
        return nr_col;
    }

    public void setNrCol(int nr_col) {
        this.nr_col = nr_col;
    }

    public Character get_current_Character() {
        return current_Character;
    }

    public Cell get_current_Cell() {
        return current_Cell;
    }

    public void set_current_Cell(Cell current_Cell) {
        this.current_Cell = current_Cell;
    }

    public Cell getCell(int row, int col) {
        if (row >= 0 && row < nr_lin && col >= 0 && col < nr_col) {
            return get(row).get(col);
        } else {
            throw new IndexOutOfBoundsException("Invalid cell coordinates!");
        }
    }

    public void setCell(int row, int col, Cell cell) {
        if (row >= 0 && row < nr_lin && col >= 0 && col < nr_col) {
            get(row).set(col, cell);
        } else {
            throw new IndexOutOfBoundsException("Invalid cell coordinates!");
        }
    }

    public Grid getInstance(){
        return instance;
    }

    public void setInstance(Grid instance){
        this.instance = instance;
    }
}

class Cell {
    private int poz_x, poz_y;
    private CellEntityType type;
    private int visited;

    public Cell(int poz_x, int poz_y, CellEntityType type, int visited) {
        this.poz_x = poz_x;
        this.poz_y = poz_y;
        this.type = type;
        this.visited = visited;
    }

    public int getPozX() {
        return poz_x;
    }

    public void setPozX(int poz_x) {
        this.poz_x = poz_x;
    }

    public int getPozY() {
        return poz_y;
    }

    public void setPozY(int poz_y) {
        this.poz_y = poz_y;
    }

    public CellEntityType getType() {
        return type;
    }

    public int getVisited() {
        return visited;
    }

    public void setType(CellEntityType type) {
        this.type = type;
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }
}

class Account {
    private Information account_data;
    private ArrayList<Character> account_Characters;
    private int games_played;

    public Information getAccountData() {
        return account_data;
    }

    public ArrayList<Character> getAccountCharacters() {
        return account_Characters;
    }

    public int getGamesPlayed() {
        return games_played;
    }

    public Account(ArrayList<Character> account_Characters, int games_played, Information account_data) {
        this.account_Characters = account_Characters;
        this.games_played = games_played;
        this.account_data = account_data;
    }

    static class Information {
        private Credentials login_data;
        private String name, country;
        private SortedSet<String> favorite_games;

        public Credentials getLoginData() {
            return login_data;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public SortedSet<String> getFavoriteGames() {
            return favorite_games;
        }

        public Information(Info_Builder builder) {
            this.login_data = builder.login_data;
            this.favorite_games = builder.favorite_games;
            this.name = builder.name;
            this.country = builder.country;
        }

        public static class Info_Builder {
            private Credentials login_data;
            private SortedSet<String> favorite_games;
            private String name;
            private String country;
            public Info_Builder() {}
            public Info_Builder setLogin_data(Credentials login_data) {
                this.login_data = login_data;
                return this;
            }
            public Info_Builder setFavorite_games(SortedSet<String> favorite_games) {
                this.favorite_games = favorite_games;
                return this;
            }
            public Info_Builder setName(String name) {
                this.name = name;
                return this;
            }
            public Info_Builder setCountry(String country) {
                this.country = country;
                return this;
            }
            public Information build() {
                return new Information(this);
            }
        }
    }
}

class Credentials {
    private String mail, psw;

    public String getMail() {
        return mail;
    }

    public String getPsw() {
        return psw;
    }

    public Credentials(String mail, String psw) {
        this.mail = mail;
        this.psw = psw;
    }
}
