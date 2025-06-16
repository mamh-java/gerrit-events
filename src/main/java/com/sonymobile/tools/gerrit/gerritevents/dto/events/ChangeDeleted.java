/*
 *  The MIT License
 *
 *  Copyright 2010 Sony Ericsson Mobile Communications. All rights reserved.
 *  Copyright 2012 Sony Mobile Communications AB. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.sonymobile.tools.gerrit.gerritevents.dto.events;

import com.sonymobile.tools.gerrit.gerritevents.dto.GerritEventType;
import com.sonymobile.tools.gerrit.gerritevents.dto.attr.Account;
import net.sf.json.JSONObject;

import static com.sonymobile.tools.gerrit.gerritevents.dto.GerritEventKeys.DELETER;

/**
 * A DTO representation of the change-deleted Gerrit Event.
 */
public class ChangeDeleted extends ChangeBasedEvent {

    /**
     * The person who triggered this event.
     */
    private Account deleter;

    /**
     * Default constructor.
     */
    public ChangeDeleted() {
    }

    /**
     * Get the deleter who triggered this event.
     * @return the deleter
     */
    public Account getDeleter() {
        return this.deleter;
    }

    /**
     * Set the deleter for this event.
     * @param deleter the deleter to set
     */
    public void setDeleter(Account deleter) {
        this.deleter = deleter;
    }

    /**
     * Constructor that fills data directly.
     * @param json the JSON Object
     * @see #fromJson(JSONObject)
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ChangeDeleted(JSONObject json) {
        fromJson(json);
    }

    @Override
    public GerritEventType getEventType() {
        return GerritEventType.CHANGE_DELETED;
    }

    @Override
    public boolean isScorable() {
        return false;
    }

    @Override
    public void fromJson(JSONObject json) {
        super.fromJson(json);

        if (!json.containsKey(DELETER)) {
            return;
        }
        deleter = new Account(json.getJSONObject(DELETER));
    }
}
