import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
//import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.osbot.rs07.api.Bank.BankMode;
import org.osbot.rs07.api.filter.AreaFilter;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.RandomBehaviourHook;
import org.osbot.rs07.script.RandomEvent;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import org.osbot.rs07.api.model.Player;


@ScriptManifest(name = "ChaosDruidzSlayaZ", author = "Dokato", version = 4.1, info = "Kills Chaos Druids in Ardougne", logo = "")
public class MainDruidz extends Script {
	
	private static final Color customRed = new Color(255, 0, 0, 150);
	private static final Color customGreen = new Color(0, 255, 51, 150);
	private static final Color standardTxtColor = new Color(255, 255, 255);
	private static final Color breakRectColor = new Color(0, 0, 0, 175);
	
	private boolean tradeSwitch1;
	private boolean tradeSwitch2;
	private boolean tradeSwitch3;
	private static final Rectangle tradeRect1 = new Rectangle(450, 8, 20, 20);
	private static final Rectangle tradeRect2 = new Rectangle(420, 8, 20, 20);
	private static final Rectangle tradeRect3 = new Rectangle(390, 8, 20, 20);
	
	private boolean resetSwitch1;
	private boolean resetSwitch2;
	private boolean resetSwitch3;
	private static final Rectangle resetRect1 = new Rectangle(480, 265, 20, 20);
	private static final Rectangle resetRect2 = new Rectangle(450, 265, 20, 20);
	private static final Rectangle resetRect3 = new Rectangle(420, 265, 20, 20);
	
	private static final Rectangle breakRect = new Rectangle(190, 110, 390, 50);
	
	private static final Area GE = new Area(3158,3495,3171,3483);
	private static final Area DRUIDZ_AREA = new Area(2564, 3354, 2560, 3358);
	private static final Area DRUIDZ_DUNGEON = new Area(2560, 9759, 2572, 9749);
	private static final Area BANK_AREA = new Area(2612, 3330, 2621, 3336);
	
	private static final long milisecondsPerMinute = 60000; 
	private static final long bottingTime = 52 * milisecondsPerMinute;
	private static final long breakingTime = 14 * milisecondsPerMinute;
	private static final long randomizeValue = 5 * milisecondsPerMinute;
	
	private boolean startb = true;
	
	private long timeBegan;
	private long timeRan;
	private long timeReset;
	private long timeSinceReset;
	private long timeBotted;
	private long timeOffline;
	
	private boolean showTradeTime;
	
	private long timeLastTraded;
	private long timeSinceLastTraded;
	
	private long timeLastBreaked;
	private long timeSinceLastBreaked;
	private long timeBreakStart;
	
	private long timeBotting;
	private long timeBreaing;
	
	private boolean resetBreakCheck = true;
	private boolean hasStarted = false;
	
	private String status;
	
	private int trades;
	private int bankTrips;
	private int i;
	private int profitBanked;
	private boolean allHerbs;
	private boolean rannarOnly;
	//private Random randomGenerator = new Random();
	
	private String muler;
	
	private boolean sendTradeRequest;
	private boolean checkHasWithdrawn;
	
	private String trainStatus;
	
	public void onStart() {
		resetTime();
		
		status="getting muler from file";
		this.muler = Filer.getMuler();
		
		this.bankTrips = 0;
		this.profitBanked = 0;

		this.allHerbs = true;
		this.rannarOnly = false;
		
		setTradeSwitchesOFF();
		
		this.sendTradeRequest = true;
		this.checkHasWithdrawn = false;
		
		this.showTradeTime = false;
		
		this.trainStatus = "";
		
		this.timeLastBreaked = System.currentTimeMillis();
		
		getBot().addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mousePressed(MouseEvent e) {
				if(tradeRect1.contains(e.getPoint()))
					tradeSwitch1 = !tradeSwitch1;
				
				if(tradeRect2.contains(e.getPoint()))
					tradeSwitch2 = !tradeSwitch2;
				
				if(tradeRect3.contains(e.getPoint()))
					tradeSwitch3 = !tradeSwitch3;
				
				
				if(isMule()){
					if(resetRect1.contains(e.getPoint()))
						resetSwitch1 = !resetSwitch1;
					
					if(resetRect2.contains(e.getPoint()))
						resetSwitch2 = !resetSwitch2;
					
					if(resetRect3.contains(e.getPoint()))
						resetSwitch3 = !resetSwitch3;
				}
			}
			
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		try {
		    this.bot.getRandomExecutor().registerHook(new RandomBehaviourHook(RandomEvent.AUTO_LOGIN) {
		        @Override
		        public boolean shouldActivate() {
		        	if(hasStarted && needToBreak()){
		        		status="Breaking";
		        		return false;
		        	}else{
		        		status="Loging in";
		        		return super.shouldActivate();
		        	}
		        }
		    });
		} catch (Exception ex) {
		    log("something went wrong");
		}
		
		/*try {
		    this.bot.getRandomExecutor().registerHook(new RandomBehaviourHook(RandomEvent.BREAK_MANAGER) {
		        @Override
		        public boolean shouldActivate() {
		            return super.shouldActivate() && canStartBreak();
		        }
		    });
		} catch (Exception ex) {
		    //Break manager is not enabled
		    log("Failed to modify break handler");
		}*/
		
	}

	public int onLoop() throws InterruptedException {
		if(!needToBreak()){
			if (getClient().isLoggedIn()) {
				breakTimeProcedures();
				procedures();
				deselectItem();
				if(!isTradeNeeded()){
					if(!inVarrock()){
						cleanUpInv();
						missClicked();
						if (!getInventory().isFull()) {
							if (this.DRUIDZ_AREA.contains(myPlayer())) {
								pickUpLoot();
								fightingStyleProcedures();
								killDruidz();
							} else {
								walkToDruidz();
							}
						}else{
							bankProcedures();
						}
					}
				}else{
					tradeProcedures1();
				}
			}
		}else{
			doBreak();
		}
		return 100;
	}

	public void onPaint(Graphics2D g1) {
		
		if(this.startb){
    		this.startb=false;
    		this.timeBegan = System.currentTimeMillis();
    		this.timeReset = timeBegan;
    	}
    	this.timeRan = (System.currentTimeMillis() - this.timeBegan);
    	this.timeSinceReset = (System.currentTimeMillis() - this.timeReset);
    	this.timeSinceLastBreaked = System.currentTimeMillis() - this.timeLastBreaked;
		if (getClient().isLoggedIn()) {
			this.timeBotted = (this.timeSinceReset - this.timeOffline);
		} else {
			this.timeOffline = (this.timeSinceReset - this.timeBotted);
		}
		
		if(showTradeTime)
			this.timeSinceLastTraded = System.currentTimeMillis() - this.timeLastTraded;
		
		Graphics2D g = g1;
		
		int startY = 65;
		int increment = 15;
		int value = (-increment);
		int x = 20;
		
		g.setFont(new Font("Arial", 0, 13));
		g.setColor(standardTxtColor);
		g.drawString("Total runtime: " + ft(this.timeRan), x, getY(startY, value+=increment));
		value+=increment;
		value+=increment;
		g.drawString("Acc: " + getBot().getUsername().substring(0, getBot().getUsername().indexOf('@')), x,getY(startY, value+=increment));
		g.drawString("World: " + getWorlds().getCurrentWorld(),x,getY(startY, value+=increment));
		value+=increment;
		g.drawString("Version: " + getVersion(), x, getY(startY, value+=increment));
		g.drawString("Runtime: " + ft(this.timeSinceReset), x, getY(startY, value+=increment));
		g.drawString("Time botted: " + ft(this.timeBotted), x, getY(startY, value+=increment));
		if(hasStarted)
			g.drawString("Last break: " + ft(this.timeSinceLastBreaked), x, getY(startY, value+=increment));
		g.drawString("Status: " + status, x, getY(startY, value+=increment));
		value+=increment;
		g.drawString("Banktrips: " + bankTrips, x, getY(startY, value+=increment));
		g.drawString("Profit banked: " + ftProfit(this.profitBanked), x, getY(startY, value+=increment));
		value+=increment;
		g.drawString("Attk: " + getSkills().getStatic(Skill.ATTACK), x, getY(startY, value+=increment));
		g.drawString("Str: " + getSkills().getStatic(Skill.STRENGTH), x, getY(startY, value+=increment));
		g.drawString("Training: " + trainStatus, x, getY(startY, value+=increment));
		
		g.drawString("Trade: ", 400, 50);
		
		if(!isMule()){
			if(showTradeTime){
				g.drawString("Last trade: " + ft(this.timeSinceLastTraded), 330, 65);
				g.drawString("Trades: " + trades, 425, 80);
			}else if(!isMule())
				g.drawString("Not traded yet", 400, 65);
		}
		
		if(hasStarted && needToBreak()){
			g.setColor(breakRectColor);
			fillRect(g, breakRect);
			g.setColor(standardTxtColor);
			g.drawString("Have to break for: " + ft(this.timeBreaing) , 275, 130);
			g.drawString("Have been breaking for: " + ft((System.currentTimeMillis() - this.timeBreakStart)), 275, 145);
		}
			
		
		if(tradeSwitch1) g.setColor(customGreen);
		else g.setColor(customRed);
		fillRect(g, tradeRect1);
		
		if(tradeSwitch2)g.setColor(customGreen);
		else g.setColor(customRed);
		fillRect(g, tradeRect2);
		
		if(tradeSwitch3) g.setColor(customGreen);
		else g.setColor(customRed);
		fillRect(g, tradeRect3);
		
		g.setFont(new Font("Arial", 1, 13));
		if(getTradeSwitches()){
			g.setColor(customGreen);
			g.drawString("ON", 445, 52);
		}else{
			g.setColor(customRed);
			g.drawString("OFF", 445, 52);
		}
		
		if(isMule()){
			g.setColor(standardTxtColor);
			g.drawString("Reset", 430, 300);
			
			if(resetSwitch1) g.setColor(customGreen);
			else g.setColor(customRed);
			fillRect(g, resetRect1);
			
			if(resetSwitch2) g.setColor(customGreen);
			else g.setColor(customRed);
			fillRect(g, resetRect2);
			
			if(resetSwitch3) g.setColor(customGreen);
			else g.setColor(customRed);
			fillRect(g, resetRect3);
			
			if(getResetSwitches()){
				resetStruff();
				setResetSwitchesOff();
			}
		}

	}
	
	private int getY(int startY, int value){
		return startY + value;
	}
	
	private void fillRect(Graphics2D g, Rectangle rect){
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
	}
	
	public void onMessage(Message message) throws InterruptedException {
		String txt = message.getMessage().toLowerCase().trim();
		
		if(txt.contains("under attack"))
			sleep(random(600,900));
		
		if(txt.contains("Sending trade offer"))
			this.sendTradeRequest = false;
	}

	public void onExit() {
	}
	
	private boolean needToBreak(){
		status="break check";
		return (timeSinceLastBreaked > this.timeBotting) && (timeSinceLastBreaked < (this.timeBotting + this.timeBreaing));
	}
	
	private void doBreak() throws InterruptedException{
		status="Have to break";
		if(getClient().isLoggedIn()){
			resetBreakCheck=true;
			status="logging out to break";
			getLogoutTab().logOut();
			sleep(random(1000,1600));
			this.timeBreakStart = System.currentTimeMillis();
		}
	}
	
	private void breakTimeProcedures(){
		status="break time procedures";
		if(resetBreakCheck){
			resetBreakCheck=false;
			this.timeLastBreaked = System.currentTimeMillis();
			
			this.timeBotting = getBottingTime();
			this.timeBreaing = getBreakingTime();
			
			log("After " + ft(this.timeBotting) + " gonna break for " + ft(this.timeBreaing));
		}
		this.hasStarted = true;
	}
	
	private long getBottingTime(){
		status="getting bottingTime";
		return this.bottingTime + getRandomBreakValue();
	}
	
	private long getBreakingTime(){
		status="getting breakingTime";
		return this.breakingTime + getRandomBreakValue();
	}
	
	private long getRandomBreakValue(){
		status="getting random break value";
		return  ThreadLocalRandom.current().nextLong(-randomizeValue, randomizeValue);
	}
	
	private boolean getResetSwitches(){
		return resetSwitch1 && resetSwitch2 && resetSwitch3;
	}
	
	private void resetTime(){
		this.timeReset = System.currentTimeMillis();
		this.timeBotted = 0;
		this.timeOffline = 0;
	}
	
	private boolean isTradeNeeded(){
		status="returning if need to trade";
		return getTradeSwitches();
	}
	
	private void tradeProcedures1() throws InterruptedException{
		if(!isWithdrawalNeeded()){
			if(getBank().isOpen() && !isMule())
				getBank().close();
			
			if(isMule())
				muleProcedures();
			else
				traderProcedures();
		}else{
			bankProcedures();
		}
	}
	
	private boolean isMule(){
		return myPlayer().getName().equals(this.muler);
	}
	
	private void muleProcedures() throws InterruptedException{
		status="mule procedures";
		if(inVarrock()){
			status="checking if i'm in GE";
			if(!GE.contains(myPlayer().getPosition())){
				status="not in GE, go to ge";
				goToGE();
			}else{
				setTradeSwitchesOFF();
			}
		}
	}
	
	private void traderProcedures() throws InterruptedException{
		if(!isDoneTrading()){
			if(getTrade().isCurrentlyTrading()){
				status = "currently trading";
				if(getTrade().isFirstInterfaceOpen()){
					status = "first trade window ";
					if(getInventory().isEmpty()){
						status = "Accepting..";
	    				getTrade().acceptTrade();
					}else{
						status = "putting items in trade";
						for(Item item : getInventory().getItems()){
							getInventory().getItem(item.getId()).interact("Offer-All");
							sleep(random(250, 780));
						}
					}
				}else if(getTrade().isSecondInterfaceOpen()){
	    			status = "second trade window";
	    			getTrade().acceptTrade();
	    		}
			}else{
				status="check if i need to send trade request";
				if(sendTradeRequest){
					status="about to get the muler";
					Player muler = getPlayer(this.muler,7);
					status="checking if muler != null";
					if(muler != null){
						status="about to interact with muler";
						muler.interact("Trade with");
						sleep(random(4500,7500));
					}
				}
			}
		}else{
			
			this.checkHasWithdrawn = false;
			this.sendTradeRequest = true;
			
			this.trades++;
			
			this.timeLastTraded = System.currentTimeMillis();
			this.showTradeTime = true;
			
			setTradeSwitchesOFF();
			
			resetStruff();
		}
	}
	
	private void resetStruff(){
		resetTime();
		this.bankTrips = 0;
		this.profitBanked = 0;
	}
	
	private boolean isDoneTrading(){
		return !getTrade().isCurrentlyTrading() && getInventory().isEmpty() && checkHasWithdrawn;
	}
	
	private Player getPlayer(String name, int radius){
    	status = "getting the muler";
    	Player aPlayer = null;
    	status = "getting players List";
    	List<Player> playerList = getPlayers().filter(new AreaFilter<Player>(myPlayer().getArea(radius)));
    	status = "about to iterate over players list";
    	for(Player player : playerList){
			status = "iteraying over players";
			if(player.getName().equals(name)){
				aPlayer=player;
				break;
			}
		}
		return aPlayer;
    }
	
	private void goToGE(){
		status="going to GE";
		if(getMap().canReach(new Position(3165,3486,0)))
    		getWalking().walk(new Area(3165,3486,3163,3484));
    	else if(getMap().canReach(new Position(3165,3478,0)))
    		getWalking().walk(new Area(3165,3478,3163,3476));
    	else if(getMap().canReach(new Position(3165,3469,0)))
    		getWalking().walk(new Area(3164,3466,3165,3469));
    	else if(getMap().canReach(new Position(3172,3460,0)))
    		getWalking().walk(new Area(3172,3460,3174,3457));
    	else if(getMap().canReach(new Position(3180,3455,0)))
    		getWalking().walk(new Area(3182,3453,3180,3455));
    	else if(getMap().canReach(new Position(3191,3488,0)))
    		getWalking().walk(new Area(3191,3488,3193,3446));
    	else if(getMap().canReach(new Position(3196,3442,0)))
    		getWalking().walk(new Area(3196,3442,3199,3440));
    	else if(getMap().canReach(new Position(3199,3434,0)))
    		getWalking().walk(new Area(3199,3434,3198,3431));
    	else if(getMap().canReach(new Position(3207,3428,0)))
    		getWalking().walk(new Area(3209,3426,3207,3428));
    	else if(getMap().canReach(new Position(3211,3418,0)))
    		getWalking().walk(new Area(3211,3418,3210,3415));
	}
	
	private boolean inVarrock(){
		status="returning if i'm in varrock";
		return myPosition().getY() > 3410 && myPosition().getX() < 3225 
				&& myPosition().getX() > 3120;
	}
	
	private void setResetSwitchesOff(){
		resetSwitch1 = false;
		resetSwitch2 = false;
		resetSwitch3 = false;
	}
	
	private void setTradeSwitchesOFF(){
		tradeSwitch1 = false;
		tradeSwitch2 = false;
		tradeSwitch3 = false;
	}
	
	private void setTradeSwitchesON(){
		tradeSwitch1 = true;
		tradeSwitch2 = true;
		tradeSwitch3 = true;
	}
	
	private boolean getTradeSwitches(){
		return tradeSwitch1 && tradeSwitch2 && tradeSwitch3;
	}
	
	/* unnoted id:
	 * 
	 * 207 rannar
	 * 211 avantoe
	 * 219 torsol
	 * 213 kwuarm
	 * 217 dwarf weed
	 * 209 irit
	 * 2485 lantadyme
	 * 205 harralander
	 * 215 cadantine
	 * 
	 * 1621 emerald
	 * 1619 ruby
	 * 
	 * 561 nature rune
	 * 563 law rune
	 * 
	 * 830 rune javelin
	 * 9142 mithril bolts
	 * 
	 * 13471 Ensouled chaos druid head
	 * 
	 */
	private boolean isGoodItem(int itemId){
		status="returning if id is good";
		return (((((((((((((((itemId==207|| itemId==211) || itemId==219)
				|| itemId==213) || itemId==217) || itemId==9142)
				|| itemId==209) || itemId==2485) || itemId==561)
				|| itemId==563) || itemId==830) || itemId==1621)
				|| itemId==205) || itemId==1619) || itemId==215)
				|| itemId==13471);
	}
	
	public int invValue() {
		status="in invValue()";
		int value = 0;
		Item[] items = getInventory().getItems();
		GrandExchange ge = new GrandExchange();
		for(Item item : items){
			int itemId=item.getId();
			status="gonna check if item is good";
			if (isGoodItem(itemId)){
				status="about to increase the value";
				try {
					value+=item.getAmount() * ge.getSellingPrice(itemId);
				} catch (IOException e) {
					status = "Problem with getting price";
					log("Problem with getting price");
				}
			}
		}
		return value;
	}

	public void missClicked() throws InterruptedException {
		if(getWidgets().isVisible(192,1,1)){
			getWidgets().get(162,72).interact("Close");
			sleep(random(190,230));
		}else if(myPosition().getZ() == 1) {
			if (!myPlayer().isMoving()) {
				status = "Going to druidz";
				getObjects().closest("Ladder").interact("Climb-down");
				sleep(random(600, 1500));
			}
		}else if(DRUIDZ_DUNGEON.contains(myPlayer())&&!myPlayer().isMoving()) {
			status = "Going to druidz";
			 getObjects().closest("Ladder").interact("Climb-up");
			sleep(random(1000, 1500));
		}
	}

	public void deselectItem() {
		if(getInventory().isItemSelected()) {
			status = "Deselecting item";
			getInventory().deselectItem();
		}
	}

	public void wieldBolts() throws InterruptedException {
		if((this.inventory.contains(9142))&&(getSkills().getStatic(Skill.RANGED) >= 36)) {
			status = "Wielding bolts";
			getInventory().getItem(9142).interact("Wield");
			sleep(random(300, 600));
		}
	}

	public void chekLootInInv() {
		status="checking if loot in inv";
		if(getInventory().isFull()){
			this.i=1;
		}else{
		}
	}

	private void addBankedprofit() {
		status="Adding banked profit";
		if(i==1){
			profitBanked += invValue();
		}
	}

	public void incrementBankTrips() {
		status="incrementing banktrips";
		if (i==1 && getInventory().isEmpty()) {
			bankTrips += 1;
			i = 0;
		}
	}
	
	private void bankProcedures() throws InterruptedException{
		if(BANK_AREA.contains(myPlayer()))
			bank();
		else
			walkToBank();
	}
	
	public void bank() throws InterruptedException {
		status = "Banking";
		if (getBank().isOpen()) {
			if(!isTradeNeeded()){
				if (!getInventory().isEmpty()) {
					if(!isTradeNeeded()){
						//trading is not needed, normal flow
						status = "banking (bank open)";
						chekLootInInv();
						addBankedprofit();
						getBank().depositAll();
						sleep(random(600, 900));
						incrementBankTrips();
					}
				}
			}else{
				if(isWithdrawalNeeded()){
					if(!getInventory().isEmpty()){
						getBank().depositAll();
						sleep(random(600, 900));
					}else{
						withdrawAllLoot();
					}
				}
			}
		} else {
			status = "interacting with bank booth";
			if (!myPlayer().isMoving()) {
				 getObjects().closest("Bank booth").interact("Bank");
				sleep(random(600, 900));
			}
		}
	}
	
	private void withdrawAllLoot() throws InterruptedException{
		status="withdrawing all loot";
		if(getBank().enableMode(BankMode.WITHDRAW_NOTE)){
			for(Item bankItem : getBank().getItems()){
				status="iterating over each bankitem";
				if((isGoodItem(bankItem.getId()) || (bankItem.getId() == 995/*coins*/ && !isMule()))
						|| bankItem.getName().equals("Ensouled chaos druid head".trim())){
					status="withdrawing item from bank";
					getBank().withdrawAllButOne(bankItem.getId());
					sleep(random(200,400));
				}
			}
		}
		if(!isMule())
			this.checkHasWithdrawn = true;
	}
	
	/* noted id:
	 * 
	 * 208 rannar
	 * 212 avantoe
	 * 220 torsol
	 * 214 kwuarm
	 * 218 dwarf weed
	 * 210 irit
	 * 2486 lantadyme
	 * 206 harralander
	 * 216 cadantine
	 * 
	 * 1622 emerald
	 * 1620 ruby
	 * 
	 * 561 nature rune
	 * 563 law rune
	 * 
	 * 830 rune javelin
	 * 9142 mithril bolts
	 * 
	 * 13472 Ensouled chaos druid head
	 * 
	 */
	private boolean isWithdrawalNeeded(){
		status="returning if withdrawal is needed";
		return !checkHasWithdrawn && !invContainsNotedLoot() && !getTrade().isCurrentlyTrading() && !inVarrock();
	}
	
	private boolean invContainsNotedLoot(){
		return ((((((((((getInventory().contains(208)) && getInventory().contains(212))
				&& getInventory().contains(214)) && getInventory().contains(218)) 
				&& getInventory().contains(2486)) && getInventory().contains(206))
				&& getInventory().contains(216)) && getInventory().contains(561)) 
				&& getInventory().contains(563)) && getInventory().contains(9142)) 
				&& getInventory().contains(210);
	}
	
	public void walkToBank() throws InterruptedException {
		status = "Walking to bank";
		if (!DRUIDZ_AREA.contains(myPlayer())) {
			if(myPosition().getX() > 2601) {
				if(getSkills().getStatic(Skill.AGILITY)>=33){
					status = "walking to bank (east side) log";
					if(getMap().canReach(new Position(2616, 3332, 0))) {
						getWalking().walk(new Area(2614,3333,2618,3333));
					}else if (getMap().canReach(new Position(2609, 3338, 0))) {
						getWalking().walk(new Area(2608, 3337, 2609, 3339));
					}
				}else{
					status = "walking to bank (east side)";
					if(getMap().canReach(new Position(2616, 3332, 0))) {
						getWalking().walk(new Area(2614,3333,2618,3333));
					}else if(getMap().canReach(new Position(2613,3344,0))){
						getWalking().walk(new Area(2612,3344,2613,3342));
					}else if(getMap().canReach(new Position(2614,3351,0))){
						getWalking().walk(new Area(2613,3352,2614,3349));
					}else if(getMap().canReach(new Position(2613,3359,0))){
						getWalking().walk(new Area(2612,3359,2613,3356));
					}else if(getMap().canReach(new Position(2609,3367,0))){
						getWalking().walk(new Area(2607,3367,2611,3366));
					}else if(getMap().canReach(new Position(2601,3367,0))){
						getWalking().walk(new Area(2600,3368,2604,3366));
					}else if(getMap().canReach(new Position(2594,3367,0))){
						getWalking().walk(new Area(2593,3368,2596,3366));
					}
				}
			}else{
				if(getSkills().getStatic(Skill.AGILITY)>=33){
					status = "walking to bank (west side) log";
					if(new Area(2595, 3340, 2598, 3330).contains(myPlayer())){
						if(!myPlayer().isAnimating()&&!myPlayer().isMoving()){
							 getObjects().closest(new Area(2599, 3336, 2599, 3336),"Log balance").interact("Walk-across");
							 sleep(random(600, 900));
						}
					}else if (getMap().canReach(new Position(2598, 3336, 0))) {
						getWalking().walk(new Area(2596, 3338, 2598, 3334));
					}else if (getMap().canReach(new Position(2587, 3343, 0))) {
						getWalking().walk(new Area(2585, 3344, 2590, 3342));
					}else if (getMap().canReach(new Position(2580, 3346, 0))) {
						getWalking().walk(new Area(2577, 3345, 2582, 3348));
					}else if (getMap().canReach(new Position(2574, 3352, 0))) {
						getWalking().walk(new Area(2573, 3350, 2575, 3355));
					}
				}else{
					status = "walking to bank (west side)";
					if(getMap().canReach(new Position(2605,3369,0))){
						getWalking().walk(new Area(2604,3370,2607,3368));
					}else if(getMap().canReach(new Position(2592,3369,0))){
						getWalking().walk(new Area(2590,3372,2594,3367));
					}else if(getMap().canReach(new Position(2583,3368,0))){
						getWalking().walk(new Area(2580,3369,2583,3368));
					}else if(getMap().canReach(new Position(2581,3355,0))){
						getWalking().walk(new Area(2583,3354,2581,3356));
					}  
				}
			}
		}else{
			 getObjects().closest("Door").interact("Open");
			sleep(random(600, 900));
		}
	}

	public void walkToDruidz() throws InterruptedException {
		status = "Walking to druidz";
		if(inDruidzArea()){
			if(myPlayer().getX()<=2571){
				pickLock();
			}else if (myPlayer().getX() <= 2600) {
				if(getSkills().getStatic(Skill.AGILITY)>=33){
					status = "walking to druidz (west side) log";
					if(getMap().canReach(new Position(2565, 3356, 0))) {
						getWalking().walk(new Area(2566, 3355, 2567, 3356));
					}else if (getMap().canReach(new Position(2574, 3352, 0))){
						getWalking().walk(new Area(2573, 3350, 2575, 3355));
					}else if (getMap().canReach(new Position(2580, 3346, 0))){
						getWalking().walk(new Area(2577, 3345, 2582, 3348));
					}else if (getMap().canReach(new Position(2587, 3343, 0))){
						getWalking().walk(new Area(2585, 3344, 2590, 3342));
					}else if (getMap().canReach(new Position(2595, 3339, 0))){
						getWalking().walk(new Area(2593, 3340, 2596, 3339));
					}
				}else{
					status = "walking to druidz (west side)";
					if(getMap().canReach(new Position(2567,3356,0))){
						getWalking().walk(new Area(2566,3355,2568,3356));
					}else if(getMap().canReach(new Position(2573,3355,0))){
						getWalking().walk(new Area(2573,3355,2574,3352));
					}else if(getMap().canReach(new Position(2581,3359,0))){
						getWalking().walk(new Area(2581,3361,2581,3359));
					}else if(getMap().canReach(new Position(2586,3369,0))){
						getWalking().walk(new Area(2586,3369,2589,3368));
					}else if(getMap().canReach(new Position(2591,3369,0))){
						getWalking().walk(new Area(2591,3369,2593,3367));
					}
				}
			}else{
				if(getSkills().getStatic(Skill.AGILITY)>=33){
					status = "walking to druidz (east side) log";
					if(myPlayer().getX()>2600 && myPlayer().getX()<2606){
						if(!myPlayer().isAnimating()&&!myPlayer().isMoving()){
							 getObjects().closest(new Area(2601, 3336, 2601, 3336),"Log balance").interact("Walk-across");
							 sleep(random(600, 900));
						}
					}else if(getMap().canReach(new Position(2602, 3336, 0))){
						getWalking().walk(new Area(2602, 3338, 2605, 3333));
					}else if(getMap().canReach(new Position(2609, 3338, 0))){
						getWalking().walk(new Area(2608, 3337, 2609, 3339));
					}
				}else{
					status = "walking to druidz (east side)";
					if(getMap().canReach(new Position(2594,3367,0))){
						getWalking().walk(new Area(2593,3368,2596,3366));
					}else if(getMap().canReach(new Position(2601,3367,0))){
						getWalking().walk(new Area(2600,3368,2604,3366));
					}else if(getMap().canReach(new Position(2609,3367,0))){
						getWalking().walk(new Area(2607,3367,2611,3366));
					}else if(getMap().canReach(new Position(2613,3359,0))){
						getWalking().walk(new Area(2612,3359,2613,3356));
					}else if(getMap().canReach(new Position(2614,3351,0))){
						getWalking().walk(new Area(2613,3352,2614,3349));
					}else if(getMap().canReach(new Position(2613,3344,0))){
						getWalking().walk(new Area(2612,3344,2613,3342));
					}
				}
			}
		}else{
			goToDruidzArea();
		}
	}
	
	private boolean inDruidzArea(){
		return myPosition().getX() < 2629;
	}
	
	private void goToDruidzArea(){
    	status="Walking to druidz Area";
    	if(getMap().canReach(new Position(2617,3337,0)))
			getWalking().walk(new Area(2617,3337,2619,3337));
    	else if(getMap().canReach(new Position(2623,3337,0)))
			getWalking().walk(new Area(2623,3338,2627,3335));
    	else if(getMap().canReach(new Position(2629,3335,0)))
			getWalking().walk(new Area(2629,3335,2631,3337));
    	else if(getMap().canReach(new Position(2636,3338,0)))
			getWalking().walk(new Area(2636,3338,2634,3343));
    	else if(getMap().canReach(new Position(2636,3347,0)))
			getWalking().walk(new Area(2636,3347,2635,3350));
    	else if(getMap().canReach(new Position(2637,3356,0)))
			getWalking().walk(new Area(2637,3356,2636,3361));
    	else if(getMap().canReach(new Position(2637,3365,0)))
			getWalking().walk(new Area(2637,3365,2636,3369));
    	else if(getMap().canReach(new Position(2636,3372,0)))
			getWalking().walk(new Area(2636,3372,2640,3374));
    	else if(getMap().canReach(new Position(2646,3377,0)))
			getWalking().walk(new Area(2646,3377,2647,3382));
    	else if(getMap().canReach(new Position(2650,3385,0)))
			getWalking().walk(new Area(2650,3385,2655,3388));
    	else if(getMap().canReach(new Position(2656,3392,0)))
			getWalking().walk(new Area(2656,3392,2661,3394));
    	else if(getMap().canReach(new Position(2665,3396,0)))
			getWalking().walk(new Area(2665,3396,2670,3398));
    	else if(getMap().canReach(new Position(2673,3399,0)))
			getWalking().walk(new Area(2673,3399,2677,3401));
    	else if(getMap().canReach(new Position(2680,3404,0)))
			getWalking().walk(new Area(2680,3404,2685,3411));
    	else if(getMap().canReach(new Position(2687,3411,0)))
			getWalking().walk(new Area(2687,3411,2689,3417));
    	else if(getMap().canReach(new Position(2690,3418,0)))
			getWalking().walk(new Area(2690,3418,2692,3423));
    	else if(getMap().canReach(new Position(2694,3423,0)))
			getWalking().walk(new Area(2694,3423,2698,3428));
    	else if(getMap().canReach(new Position(2701,3427,0)))
			getWalking().walk(new Area(2701,3427,2705,3432));
    	else if(getMap().canReach(new Position(2706,3432,0)))
			getWalking().walk(new Area(2706,3432,2708,3437));
    	else if(getMap().canReach(new Position(2708,3438,0)))
			getWalking().walk(new Area(2708,3438,2711,3442));
    	else if(getMap().canReach(new Position(2710,3443,0)))
			getWalking().walk(new Area(2710,3443,2708,3446));
    	else if(getMap().canReach(new Position(2713,3448,0)))
			getWalking().walk(new Area(2713,3448,2712,3453));
    	else if(getMap().canReach(new Position(2715,3453,0)))
			getWalking().walk(new Area(2715,3453,2720,3455));
    	else if(getMap().canReach(new Position(2720,3456,0)))
			getWalking().walk(new Area(2720,3456,2723,3461));
    	else if(getMap().canReach(new Position(2724,3461,0)))
			getWalking().walk(new Area(2724,3461,2728,3464));
    	else if(getMap().canReach(new Position(2723,3466,0)))
			getWalking().walk(new Area(2723,3466,2721,3470));
    	else if(getMap().canReach(new Position(2723,3471,0)))
			getWalking().walk(new Area(2723,3471,2726,3476));
    	else if(getMap().canReach(new Position(2727,3476,0)))
			getWalking().walk(new Area(2727,3476,2731,3479));
    	else if(getMap().canReach(new Position(2731,3477,0)))
			getWalking().walk(new Area(2731,3477,2737,3478));
    	else if(getMap().canReach(new Position(2739,3480,0)))
			getWalking().walk(new Area(2739,3480,2744,3477));
    	else if(getMap().canReach(new Position(2746,3479,0)))
			getWalking().walk(new Area(2746,3479,2749,3475));
    	else if(getMap().canReach(new Position(2751,3478,0)))
			getWalking().walk(new Area(2751,3478,2754,3477));
    }
	
	public boolean pickUpLoot() throws InterruptedException {
		status = "getting items list on gorund";
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<GroundItem> groundLoot = this.groundItems.filter(new Filter[] { new AreaFilter(this.DRUIDZ_AREA) });
		status = "just got the list of items";
		for (GroundItem loot : groundLoot) {
			status = "entering items loop";
			int lootId = loot.getId();
			status = "just getted loodId";
			if (isGoodItem(lootId)){
				status = "should pick it up";
				if (allHerbs||(rannarOnly && lootId==207)) {
					status = "almost picking up";
					if (!myPlayer().isUnderAttack()) {
						status = "Picking up loot";
						loot.interact("Take");
						sleep(random(1000, 1500));
					}
				}
			}
		}
		return false;
	}

	public void pickLock() throws InterruptedException {
			status = "about to picklock";
			if (!myPlayer().isMoving()) {
				status = "Picklocking";
				getObjects().closest(new Area(2565, 3356, 2565, 3356),"Door").interact("Pick-lock");
				sleep(random(150, 325));
			}
	}

	private void killDruidz() throws InterruptedException {
		if ((!myPlayer().isUnderAttack()&&!getCombat().isFighting())&&!myPlayer().isAnimating()) {
			status = "getting druidz list";
			@SuppressWarnings({"unchecked","rawtypes"})
			List<NPC> monsters = getNpcs().filter(new AreaFilter(DRUIDZ_AREA));
			status = "creating iterator";
			Iterator<NPC> it = monsters.iterator();
			status = "created iterator";
			NPC toAttack = (NPC) it.next();
			status = "toAtack'ed";
			while (it.hasNext()) {
				status = "Looking for druidz";
				NPC current = (NPC) it.next();
				status = "druidz 1";
				if (current.getId() == 2878) {
					status = "druidz 2";
					if (!current.isUnderAttack() && current.getHealthPercent()!=0){
						status = "druidz 3";
						toAttack = current;
					}
				}
			}
			status = "clearing list";
			monsters.clear();
			if ((!myPlayer().isUnderAttack()&&!getCombat().isFighting())&&!myPlayer().isAnimating()) {
				status = "Killing druidz";
				toAttack.interact("Attack");
				sleep(random(1500, 2000));
			}
		}
	}

	public void buryBones() throws InterruptedException {
		if (getInventory().contains("Bones")) {
			status = "Burying bones";
			getInventory().getItem("Bones").interact("Bury");
			sleep(random(600, 900));
		}
	}

	public void dropItemsIfNeeded() throws InterruptedException {
		status = "dropping items";
		if (getInventory().contains("Vial of water")){
			getInventory().getItem("Vial of water").interact("Drop");
			sleep(random(300, 600));
		}else if(getInventory().contains("Coins")){
			getInventory().getItem("Coins").interact("Drop");
			sleep(random(300, 600));
		} else if (getInventory().contains("Air rune")){
			getInventory().getItem("Air rune").interact("Drop");
			sleep(random(300, 600));
		} else if (getInventory().contains("Earth rune")){
			getInventory().getItem("Earth rune").interact("Drop" );
			sleep(random(300, 600));
		} else if (getInventory().contains("Body rune")){
			getInventory().getItem("Body rune").interact("Drop");
			sleep(random(300, 600));
		} else if (getInventory().contains("Mind rune")){
			getInventory().getItem("Mind rune").interact("Drop");
			sleep(random(300, 600));
		} else if (getInventory().contains("Bronze longsword")){
			getInventory().getItem("Bronze longsword").interact("Drop");
			sleep(random(300, 600));
		} else if (getInventory().contains("Snape grass")){
			getInventory().getItem("Snape grass").interact("Drop");
			sleep(random(300, 600));
		} else if (getInventory().contains("Chaos talisman")) {
			getInventory().getItem("Chaos talisman").interact("Drop");
			sleep(random(300, 600));
		} else if (getInventory().contains("Unholy mould")) {
			getInventory().getItem("Unholy mould").interact("Drop");
			sleep(random(300, 600));
		}
	}

	public void cleanUpInv() throws InterruptedException {
		status = "cleaning up inventory";
		buryBones();
		wieldBolts();
		dropItemsIfNeeded();
	}

	public void procedures() throws InterruptedException {
		
		getCamera().toTop();
		if ((getSettings().getRunEnergy() >= 13)&&(!getSettings().isRunning())) {
			status = "toggling run on";
			getSettings().setRunning(true);
			sleep(random(300, 700));
		}
		getTabs().open(Tab.INVENTORY);
		sleep(random(100, 250));
	}
	
	private void fightingStyleProcedures(){
		getFightingStyleStatus();
		switchStyles();
	}
	
	private void getFightingStyleStatus(){
		switch(getFightingStyleId()){
		case 1:
			trainStatus = "str";
			break;
		case 0:
			trainStatus = "attk";
			break;
		}
	}
	
	private void switchStyles(){ 
    	switch(getRightStyle()){
    	case "str":
    		changeFightingStyle(1);
    		break;
    	case "attk":
    		changeFightingStyle(0);
    		break;
    	}
    }
    
    private String getRightStyle(){
    	int attk = getLvl(Skill.ATTACK);
    	int str = getLvl(Skill.STRENGTH);
    	if(((attk>=40 && str<40)
    			|| 
    			(attk>=50 && str<60)
    			||
    			(attk>=60 && str<70)
    			||
    			(attk>=70 && str<80)
    			||
    			(attk>=80 && str<90)
    			) && getFightingStyleId()!=1){
    		status="returned str";
    		return "str";
    	}else if(((str>=60 && attk<=40)
    			|| 
    			(str>=70 && attk<=50)
    			||
    			(str>=80 && attk<=60)
    			) && getFightingStyleId()!=0){
    		status="returned attk";
    		return "attk";
    	}else 
    		return "good";
    }
    
    public int getFightingStyleId()
    {
    	return configs.get(43);
    }
    	
    public void changeFightingStyle(int id)
    {
    	if (id == getFightingStyleId())
    		return;
    	Tab currentTab = tabs.getOpen();
    	if (currentTab != Tab.ATTACK)
    		tabs.open(Tab.ATTACK);
    	switch (id)
    	{
    	case 0:
    		widgets.get(593, 3).interact();
    		break;
    	case 1:
    		widgets.get(593, 7).interact();
    		break;
    	case 2:
    		widgets.get(593, 11).interact();
    		break;
    	case 3:
    		widgets.get(593, 15).interact();
    		break;
    	}
    	if (currentTab != Tab.ATTACK)
    		tabs.open(currentTab);
    }
    
    private int getLvl(Skill skill){
    	return getSkills().getStatic(skill);
    }
    
    private String ftProfit(int price){
    	String priceStr = "" + price;
    	if(price >= 10000)
    		priceStr = (price / 1000) + "K";
    	else if(price >= 1000000)
    		priceStr = (price / 1000000) + "M";
    	return priceStr;	
    }
	
	private String ft(long duration) {
		String res = "";
		long days = TimeUnit.MILLISECONDS.toDays(duration);
		long hours = TimeUnit.MILLISECONDS.toHours(duration)
				- TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
						.toHours(duration));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
						.toMinutes(duration));
		if (days == 0L) {
			res = hours + ":" + minutes + ":" + seconds;
		} else {
			res = days + ":" + hours + ":" + minutes + ":" + seconds;
		}
		return res;
	}
}
