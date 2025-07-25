/*
 *  The MIT License
 *
 *  Copyright 2010 Sony Mobile Communications Inc. All rights reserved.
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
package com.sonymobile.tools.gerrit.gerritevents.dto;

import com.sonymobile.tools.gerrit.gerritevents.dto.events.ChangeAbandoned;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.ChangeMerged;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.ChangeRestored;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.CommentAdded;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.DraftPublished;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.MergeFailed;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.PatchsetCreated;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.RefReplicated;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.RefReplicationDone;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.RefUpdated;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.ReviewerAdded;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.TopicChanged;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.ProjectCreated;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.PatchsetNotified;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.PrivateStateChanged;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.WipStateChanged;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.HashtagsChanged;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.VoteDeleted;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.RerunCheck;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.ChangeDeleted;

import java.util.LinkedList;
import java.util.List;

/**
 * Representation of the type of event, if they are interesting and what class to use to parse the JSON string.
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public enum GerritEventType {

    /**
     * A patchset-created event.
     */
    PATCHSET_CREATED("patchset-created", true, PatchsetCreated.class),
    /**
     * A draft-published event.
     */
    DRAFT_PUBLISHED("draft-published", true, DraftPublished.class),
    /**
     * A change-abandoned event.
     */
    CHANGE_ABANDONED("change-abandoned", true, ChangeAbandoned.class),
    /**
     * A change-merged event.
     */
    CHANGE_MERGED("change-merged", true, ChangeMerged.class),
    /**
     * A change-restored event.
     */
    CHANGE_RESTORED("change-restored", true, ChangeRestored.class),
    /**
     * A change-deleted event.
     */
    CHANGE_DELETED("change-deleted", true, ChangeDeleted.class),
    /**
     * A comment-added event.
     */
    COMMENT_ADDED("comment-added", true, CommentAdded.class),
    /**
     * A ref-updated event.
     */
    REF_UPDATED("ref-updated", true, RefUpdated.class),
    /**
     * Replication Plugin: A ref-replicated event.
     */
    REF_REPLICATED("ref-replicated", true, RefReplicated.class),
    /**
     * Replication Plugin: A ref-replication-done event.
     */
    REF_REPLICATION_DONE("ref-replication-done", true, RefReplicationDone.class),
    /**
     * Notify PatchSet Plugin: A patchset-notified event.
     */
    PATCHSET_NOTIFIED("patchset-notified", true, PatchsetNotified.class),
    /***
     * A project-created event.
     */
    PROJECT_CREATED("project-created", true, ProjectCreated.class),
    /***
     * A topic-changed event.
     */
    TOPIC_CHANGED("topic-changed", true, TopicChanged.class),
    /***
     * A reviewer-added event.
     */
    REVIEWER_ADDED("reviewer-added", true, ReviewerAdded.class),
    /***
     * A merge-failed event.
     */
    MERGE_FAILED("merge-failed", true, MergeFailed.class),

    /***
     * A private state changed event.
     */
    PRIVATE_STATE_CHANGED("private-state-changed", true, PrivateStateChanged.class),

    /***
     * A work in progress state changed event.
     */
    WIP_STATE_CHANGED("wip-state-changed", true, WipStateChanged.class),

    /**
     * A hashtags changed event.
     */
    HASHTAGS_CHANGED("hashtags-changed", true, HashtagsChanged.class),

    /**
     * A vote deleted event.
     */
    VOTE_DELETED("vote-deleted", true, VoteDeleted.class),

    /**
     * A rerun-check event.
     */
    RERUN_CHECK("rerun-check", true, RerunCheck.class);

    private String typeValue;
    private boolean interesting;
    private Class<? extends GerritJsonEvent> eventRepresentative;

    /**
     * Constructs an instance of the enum.
     * @param typeValue The value of the type property in the JSON object.
     * @param interesting If this event type is interesting from a functionality perspective.
     * @param eventRepresentative the DTO class that represents this kind of event.
     */
    GerritEventType(String typeValue, boolean interesting,
                            Class<? extends GerritJsonEvent> eventRepresentative) {
        this.typeValue = typeValue;
        this.interesting = interesting;
        this.eventRepresentative = eventRepresentative;
    }

    /**
     * The value of the type property in the JSON object.
     * @return the type-value.
     */
    public String getTypeValue() {
        return typeValue;
    }

    /**
     * If this event type is interesting from a functionality perspective.
     * @return true if it is interesting.
     */
    public boolean isInteresting() {
        return interesting;
    }

    /**
     * Set if event type is intresting or not.
     * @param interesting true if intresting, false otherwise.
     */
    public void setInteresting(boolean interesting) {
        this.interesting = interesting;
    }

    /**
     * Gets the DTO class that represents this kind of event.
     * @return the class object.
     */
    public Class<? extends GerritJsonEvent> getEventRepresentative() {
        return eventRepresentative;
    }

    /**
     * Finds the event type for the specified type-value.
     * @param typeValue the value of the JSON object's type property.
     * @return the event type or null if nothing was found.
     */
    public static GerritEventType findByTypeValue(String typeValue) {
        for (GerritEventType type : values()) {
            if (type.getTypeValue().equalsIgnoreCase(typeValue)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Lists all event types that are interesting.
     * @return The interesting event-types.
     * @see #isInteresting()
     */
    public static GerritEventType[] getInterestingEventTypes() {
        List<GerritEventType> list = new LinkedList<GerritEventType>();
        for (GerritEventType type : values()) {
            if (type.isInteresting()) {
                list.add(type);
            }
        }
        return list.toArray(new GerritEventType[list.size()]);
    }
}
