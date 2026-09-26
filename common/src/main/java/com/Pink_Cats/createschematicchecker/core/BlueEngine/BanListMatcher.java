package com.Pink_Cats.createschematicchecker.core.BlueEngine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Matches exact IDs and namespace-scoped '*' globs used by the ID lists. */
public final class BanListMatcher {
    private static final Pattern RESOURCE_ID = Pattern.compile(
            "(?<![a-z0-9_./-])([a-z0-9_.-]+:[a-z0-9_./-]+)(?![a-z0-9_./-])");

    private BanListMatcher() {
    }

    public static boolean matchesId(String id, String[] rules) {
        if (id == null || rules == null) {
            return false;
        }
        for (String rule : rules) {
            if (matchesRule(id, rule)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsMatch(String data, String[] rules) {
        if (data == null || rules == null) {
            return false;
        }
        for (String rule : rules) {
            if (rule != null && !rule.isEmpty() && rule.indexOf('*') < 0 && data.contains(rule)) {
                return true;
            }
        }
        return countWildcardIds(data, rules) > 0;
    }

    public static int countMatches(String data, String[] rules) {
        if (data == null || rules == null) {
            return 0;
        }
        int count = 0;
        for (String rule : rules) {
            if (rule == null || rule.isEmpty() || rule.indexOf('*') >= 0) {
                continue;
            }
            int index = 0;
            while ((index = data.indexOf(rule, index)) >= 0) {
                count++;
                index += rule.length();
            }
        }
        return count + countWildcardIds(data, rules);
    }

    private static int countWildcardIds(String data, String[] rules) {
        boolean hasWildcard = false;
        for (String rule : rules) {
            if (isWildcardRule(rule)) {
                hasWildcard = true;
                break;
            }
        }
        if (!hasWildcard) {
            return 0;
        }

        int count = 0;
        Matcher matcher = RESOURCE_ID.matcher(data);
        while (matcher.find()) {
            String id = matcher.group(1);
            boolean literalAlreadyCounted = false;
            boolean wildcardMatch = false;
            for (String rule : rules) {
                if (rule == null || rule.isEmpty()) {
                    continue;
                }
                if (rule.indexOf('*') < 0 && id.contains(rule)) {
                    literalAlreadyCounted = true;
                } else if (matchesWildcardRule(id, rule)) {
                    wildcardMatch = true;
                }
            }
            if (wildcardMatch && !literalAlreadyCounted) {
                count++;
            }
        }
        return count;
    }

    private static boolean matchesRule(String id, String rule) {
        if (rule == null) {
            return false;
        }
        return rule.indexOf('*') < 0 ? id.equals(rule) : matchesWildcardRule(id, rule);
    }

    private static boolean isWildcardRule(String rule) {
        if (rule == null || rule.indexOf('*') < 0) {
            return false;
        }
        int separator = rule.indexOf(':');
        if (separator <= 0 || separator == rule.length() - 1 || rule.indexOf(':', separator + 1) >= 0) {
            return false;
        }
        for (int i = 0; i < rule.length(); i++) {
            char c = rule.charAt(i);
            if (i == separator) {
                continue;
            }
            if (c == '*' && i > separator) {
                continue;
            }
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '.' || c == '-') {
                continue;
            }
            if (c == '/' && i > separator) {
                continue;
            }
            return false;
        }
        return true;
    }

    private static boolean matchesWildcardRule(String id, String rule) {
        if (!isWildcardRule(rule)) {
            return false;
        }
        int separator = rule.indexOf(':');
        if (id.length() <= separator + 1 || !id.regionMatches(0, rule, 0, separator + 1)) {
            return false;
        }
        for (int i = separator + 1; i < id.length(); i++) {
            char c = id.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
                    || c == '_' || c == '.' || c == '-' || c == '/')) {
                return false;
            }
        }
        String path = id.substring(separator + 1);
        String glob = rule.substring(separator + 1);
        int pathIndex = 0;
        int globIndex = 0;
        int starIndex = -1;
        int retryIndex = -1;
        while (pathIndex < path.length()) {
            if (globIndex < glob.length() && glob.charAt(globIndex) == path.charAt(pathIndex)) {
                pathIndex++;
                globIndex++;
            } else if (globIndex < glob.length() && glob.charAt(globIndex) == '*') {
                starIndex = globIndex++;
                retryIndex = pathIndex;
            } else if (starIndex >= 0) {
                globIndex = starIndex + 1;
                pathIndex = ++retryIndex;
            } else {
                return false;
            }
        }
        while (globIndex < glob.length() && glob.charAt(globIndex) == '*') {
            globIndex++;
        }
        return globIndex == glob.length();
    }
}
