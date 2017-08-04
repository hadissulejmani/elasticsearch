/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.plugin.sql.action;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.CompositeIndicesRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.Objects;

import static org.elasticsearch.action.ValidateActions.addValidationError;

public class SqlRequest extends ActionRequest implements CompositeIndicesRequest {

    public static final DateTimeZone DEFAULT_TIME_ZONE = DateTimeZone.UTC;
    // initialized on the first request
    private String query;
    private DateTimeZone timeZone = DEFAULT_TIME_ZONE;
    // initialized after the plan has been translated
    private String sessionId;

    public SqlRequest() {}

    public SqlRequest(String query, DateTimeZone timeZone, String sessionId) {
        this.query = query;
        this.timeZone = timeZone;
        this.sessionId = sessionId;
    }

    @Override
    public ActionRequestValidationException validate() {
        ActionRequestValidationException validationException = null;
        if (!Strings.hasText(query)) {
            validationException = addValidationError("sql query is missing", validationException);
        }
        if (timeZone == null) {
            validationException = addValidationError("timezone is missing", validationException);
        }
        return validationException;
    }

    public String query() {
        return query;
    }

    public String sessionId() {
        return sessionId;
    }

    public DateTimeZone timeZone() {
        return timeZone;
    }

    public SqlRequest query(String query) {
        this.query = query;
        return this;
    }

    public SqlRequest sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public SqlRequest timeZone(DateTimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        query = in.readString();
        timeZone = DateTimeZone.forID(in.readString());
        sessionId = in.readOptionalString();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeString(query);
        out.writeString(timeZone.getID());
        out.writeOptionalString(sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, sessionId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        SqlRequest other = (SqlRequest) obj;
        return Objects.equals(query, other.query) 
                && Objects.equals(sessionId, other.sessionId);
    }

    @Override
    public String getDescription() {
        return "SQL [" + query + "/" + sessionId + "]";
    }
}