/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.cache.PageFragmentCache;

import java.io.Serializable;

/**
 * Holds the nodes that are displayed in the site menu
 *
 * TODO: Caches the menu in the session, better would be a page fragment cache.
 *
 * @author Christian Bauer
 */
@Name("menu")
@Scope(ScopeType.SESSION)
public class Menu implements Serializable {

    public static final String CACHE_REGION = "wiki.MainMenu";
    public static final String CACHE_KEY = "MainMenuForAccessLevel";

    @Logger
    Log log;

    @In
    WikiDirectory wikiRoot;

    @In
    WikiNodeDAO wikiNodeDAO;

    @In("#{preferences.get('Wiki')}")
    WikiPreferences wikiPreferences;

    @In
    Integer currentAccessLevel;

    NestedSetNodeWrapper<WikiDirectory> root;
    public NestedSetNodeWrapper<WikiDirectory> getRoot() {
        if (root == null) {
            refreshRoot();
        }
        return root;
    }

    @Observer(value = { "Node.updated", "Node.removed", "PersistenceContext.filterReset" }, create = false)
    public void refreshRoot() {
        log.debug("Loading menu items tree");
        root = wikiNodeDAO.findMenuItemTree(
                wikiRoot,
                wikiPreferences.getMainMenuDepth(), 
                wikiPreferences.getMainMenuLevels(),
                wikiPreferences.isMainMenuShowAdminOnly()
        );
    }

    public String getCacheRegion() {
        return CACHE_REGION;
    }

    public String getCacheKey() {
        return CACHE_KEY + currentAccessLevel;
    }

    @Observer(value = { "Node.updated", "Node.removed"})
    public void invalidateCache() {
        PageFragmentCache.instance().removeAll(CACHE_REGION);
    }

}
