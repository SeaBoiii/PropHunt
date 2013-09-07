package me.tomski.language;

public enum MessageBank {

    //Game Messages
    TOGGLE_BLOCK_LOCK_ON(getCfgMsg("toggle_block_lock_on")),
    TOGGLE_BLOCK_LOCK_OFF(getCfgMsg("toggle_block_lock_off")),

    NO_ITEM_SHARING(getCfgMsg("no_item_sharing")),
    NO_GAME_COMMANDS(getCfgMsg("no_game_comamnds")),
    DISGUISES_BLOWN(getCfgMsg("disguises_blown")),
    GAME_TIME_LEFT(getCfgMsg("game_time_left")),
    SEEKER_DELAY(getCfgMsg("seeker_delay")),
    SEEKER_DELAY_END(getCfgMsg("seeker_delay_end")),
    SOLID_BLOCK(getCfgMsg("solid_block")),
    BROKEN_SOLID_BLOCK(getCfgMsg("broken_solid_block")),

    HIDER_DEATH_MESSAGE(getCfgMsg("hider_death_message")),
    SEEKER_DEATH_MESSAGE(getCfgMsg("seeker_death_message")),
    TIME_INCREASE_MESSAGE(getCfgMsg("time_increase_message")),
    SEEKER_LIVES_MESSAGE(getCfgMsg("seeker_lives_message")),
    QUIT_GAME_MESSAGE(getCfgMsg("quit_game_message")),
    GAME_START_MESSAGE_HIDERS(getCfgMsg("game_start_message_hiders")),
    GAME_START_MESSAGE_SEEKERS(getCfgMsg("game_start_message_seeker")),
    SPECTATING(getCfgMsg("spectating")),
    JOIN_LOBBY_MESSAGE(getCfgMsg("join_lobby_message")),

    SERVER_FULL_MESSAGE(getCfgMsg("server_full_message")),
    SERVER_STATUS_IN_GAME_MESSAGE(getCfgMsg("motd_in_game")),
    SERVER_STATUS_IN_LOBBY_MESSAGE(getCfgMsg("motd_in_lobby")),

    HOSTING_AUTO_CANT_HOST(getCfgMsg("hosting_automatically_cant_host")),
    HOST_AUTO_BROADCAST_DEDI(getCfgMsg("hosting_automatically_broadcast_dedicated")),
    HOST_AUTO_BROADCAST(getCfgMsg("hosting_automatically_broadcast")),
    GAME_ALREADY_HOSTED(getCfgMsg("game_already_hosted")),
    GAME_CANT_HOST(getCfgMsg("game_cant_host")),
    GAME_HOST(getCfgMsg("game_host")),
    BROADCAST_HOST(getCfgMsg("host_broadcast")),
    ARENA_NOT_READY(getCfgMsg("arena_not_ready_for_hosting")),
    NOT_ENOUGH_PLAYERS(getCfgMsg("not_enough_players")),
    BROADCAST_FIRST_SEEKER(getCfgMsg("broadcast_first_seeker")),
    PLAYER_JOIN_LOBBY(getCfgMsg("player_join_lobby")),
    STARTING_IN_60(getCfgMsg("starting_in_60")),
    STARTING_IN_60_DEDI(getCfgMsg("starting_in_60_dedi")),
    HIDERS_WON_TIME(getCfgMsg("hiders_won_time")),
    HIDERS_WON_SEEKERS_QUIT(getCfgMsg("hiders_won_seekers_quit")),
    HIDERS_WON_KILLS(getCfgMsg("hiders_won_kills")),
    HIDERS_WON(getCfgMsg("hiders_won")),
    SEEKERS_WON_HIDERS_QUIT(getCfgMsg("seekers_won_hiders_quit")),
    SEEKERS_WON(getCfgMsg("seekers_won")),
    HOST_ENDED(getCfgMsg("host_ended")),

    DISGUISE_ERROR(getCfgMsg("disguise_error")),
    DISGUISE_MESSAGE(getCfgMsg("disguise_message")),

    SHOP_CHOSEN_DISGUISE(getCfgMsg("shop_chosen_disguise")),
    NO_BLOCK_CHOICE_PERMISSION(getCfgMsg("no_block_choice_permission")),
    NOT_IN_LOBBY(getCfgMsg("not_in_lobby")),
    ALREADY_PURCHASED_ITEM(getCfgMsg("already_purchased_item")),
    NOT_ENOUGH_CURRENCY(getCfgMsg("not_enough_currency")),
    PURCHASE_COMPLETE(getCfgMsg("purchase_complete")),

    SEEKER_SPAWN_SET(getCfgMsg("seeker_spawn_set")),
    HIDER_SPAWN_SET(getCfgMsg("hider_spawn_set")),
    LOBBY_SPAWN_SET(getCfgMsg("lobby_spawn_set")),
    SPECTATOR_SPAWN_SET(getCfgMsg("spectator_spawn_set")),
    EXIT_SPAWN_SET(getCfgMsg("exit_spawn_set")),
    ARENA_COMPLETE(getCfgMsg("arena_complete")),
    BLOCK_ACCESS_IN_GAME(getCfgMsg("block_access_in_game")),
    NEW_SEEKER_CHOSEN(getCfgMsg("new_seeker_chosen"));


    private String msg;


    MessageBank(String s) {
        this.msg = s;
    }


    private static String getCfgMsg(String string) {
        return LanguageManager.getMessageFromFile(string);
    }

    public String getMsg() {
        return msg;
    }

}
