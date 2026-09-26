package com.Pink_Cats.createschematicchecker.core.BlueEngine;

public final class BanListMatcherTest {
    private BanListMatcherTest() {
    }

    public static void main(String[] args) {
        String[] wildcard = {"create:*_valve_handle"};
        check(BanListMatcher.matchesId("create:copper_valve_handle", wildcard));
        check(BanListMatcher.matchesId("create:_valve_handle", wildcard));
        check(!BanListMatcher.matchesId("create:valve_handle", wildcard));
        check(!BanListMatcher.matchesId("other:copper_valve_handle", wildcard));
        check(!BanListMatcher.matchesId("create:copper_valve_handle_extra", wildcard));
        check(!BanListMatcher.matchesId("create:copper:valve_handle", wildcard));
        check(BanListMatcher.matchesId("create:brass_valve_handle", new String[]{"create:*_valve*"}));
        check(!BanListMatcher.matchesId("create:brass_valve_handle", new String[]{"*:brass_valve_handle"}));
        check(!BanListMatcher.matchesId("create:brass_valve_handle", new String[]{"create:brass_valve_handle*?"}));

        String[] exact = {"create:creative_crate"};
        check(BanListMatcher.matchesId("create:creative_crate", exact));
        check(!BanListMatcher.matchesId("create:creative_crate_extra", exact));
        check(BanListMatcher.containsMatch("{id:\"create:creative_crate\"}", exact));
        check(BanListMatcher.countMatches("{id:\"create:creative_crate\"}", exact) == 1);

        String data = "{Name:\"create:copper_valve_handle\",nbt:{id:\"create:brass_valve_handle\"}}";
        check(BanListMatcher.containsMatch(data, wildcard));
        check(BanListMatcher.countMatches(data, wildcard) == 2);
        check(BanListMatcher.countMatches(data, new String[]{
                "create:copper_valve_handle", "create:*_valve_handle", "create:*_valve_handle"}) == 2);
        check(BanListMatcher.countMatches("{id:\"create:gearbox\"}", wildcard) == 0);
        check(!BanListMatcher.containsMatch("{id:\"other:copper_valve_handle\"}", wildcard));
        check(BanListMatcher.countMatches("Item.of('tacz:ammo_box', '{Creative:1b}')",
                new String[]{"Item.of('tacz:ammo_box', '{Creative:1b}')"}) == 1);
    }

    private static void check(boolean condition) {
        if (!condition) {
            throw new AssertionError("BanListMatcher regression failed");
        }
    }
}
