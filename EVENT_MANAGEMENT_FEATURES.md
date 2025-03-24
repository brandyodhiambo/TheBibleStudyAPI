# Event Management Features

This document describes the new Event Management features added to the Bible API.

## Overview

The Event Management features allow group leaders to schedule study sessions, members to RSVP to these sessions, and the system to send notifications about upcoming sessions.

## Features

### 1. Schedule Study Sessions

Group leaders can schedule recurring or one-time Bible study sessions with dates, times, and locations.

- **Create Session**: Group leaders can create new study sessions with details like title, description, date, time, location, and recurrence pattern.
- **Update Session**: Group leaders can update existing sessions.
- **Delete Session**: Group leaders can delete sessions.
- **Recurrence Patterns**: Sessions can be one-time or recurring (daily, weekly, bi-weekly, monthly, or custom).
- **Session Types**: Sessions can be virtual, in-person, or hybrid.

### 2. RSVP for Sessions

Members can RSVP to study sessions to indicate attendance.

- **Submit RSVP**: Members can submit an RSVP with a status (attending, not attending, maybe) and an optional comment.
- **Update RSVP**: Members can update their RSVP status.
- **Delete RSVP**: Members can delete their RSVP.
- **View RSVPs**: Group leaders can view all RSVPs for a session.
- **RSVP Counts**: The system tracks the number of attendees for each session.

### 3. Event Notifications

The system sends reminders or updates for upcoming sessions via emails.

- **Session Reminders**: Automatic reminders are sent to group members the day before a session.
- **Manual Reminders**: Administrators can manually trigger reminders.

## API Endpoints

### Study Session Endpoints

- `POST /api/v1/sessions`: Create a new study session (requires ROLE_LEADER or ROLE_ADMIN)
- `PUT /api/v1/sessions/{sessionId}`: Update an existing study session (requires ROLE_LEADER or ROLE_ADMIN)
- `DELETE /api/v1/sessions/{sessionId}`: Delete a study session (requires ROLE_LEADER or ROLE_ADMIN)
- `GET /api/v1/sessions/{sessionId}`: Get a study session by ID
- `GET /api/v1/sessions/group/{groupId}`: Get all study sessions for a group
- `GET /api/v1/sessions/group/{groupId}/upcoming`: Get upcoming study sessions for a group
- `GET /api/v1/sessions/user/upcoming`: Get upcoming study sessions for the current user
- `GET /api/v1/sessions/date-range`: Get study sessions by date range
- `GET /api/v1/sessions/group/{groupId}/date-range`: Get study sessions by date range for a specific group
- `POST /api/v1/sessions/send-reminders`: Manually trigger sending session reminders (requires ROLE_ADMIN)

### RSVP Endpoints

- `POST /api/v1/rsvps/sessions/{sessionId}`: Submit an RSVP for a session
- `PUT /api/v1/rsvps/{rsvpId}`: Update an existing RSVP
- `DELETE /api/v1/rsvps/{rsvpId}`: Delete an RSVP
- `GET /api/v1/rsvps/{rsvpId}`: Get an RSVP by ID
- `GET /api/v1/rsvps/sessions/{sessionId}`: Get all RSVPs for a session
- `GET /api/v1/rsvps/sessions/{sessionId}/status/{status}`: Get all RSVPs for a session with a specific status
- `GET /api/v1/rsvps/user`: Get all RSVPs by the current user
- `GET /api/v1/rsvps/sessions/{sessionId}/user`: Get the current user's RSVP for a specific session
- `GET /api/v1/rsvps/sessions/{sessionId}/count/{status}`: Count RSVPs for a session by status

## Data Models

### Study Session

- `id`: The ID of the session
- `title`: The title of the session
- `description`: A description of the session
- `sessionDate`: The date of the session
- `startTime`: The start time of the session
- `endTime`: The end time of the session
- `location`: The location of the session
- `type`: The type of session (VIRTUAL, IN_PERSON, HYBRID)
- `recurrencePattern`: The recurrence pattern of the session (NONE, DAILY, WEEKLY, BI_WEEKLY, MONTHLY, CUSTOM)
- `recurrenceEndDate`: The end date for recurring sessions
- `group`: The group the session belongs to
- `createdBy`: The user who created the session
- `rsvps`: The RSVPs for the session
- `createdAt`: When the session was created
- `updatedAt`: When the session was last updated

### Session RSVP

- `id`: The ID of the RSVP
- `session`: The session being RSVP'd to
- `user`: The user making the RSVP
- `status`: The RSVP status (ATTENDING, NOT_ATTENDING, MAYBE)
- `comment`: Optional comment from the user
- `createdAt`: When the RSVP was created
- `updatedAt`: When the RSVP was last updated

## Implementation Details

The Event Management features are implemented using the following components:

- **Models**: StudySession, SessionRSVP, SessionType, RecurrencePattern, RSVPStatus
- **Repositories**: StudySessionRepository, SessionRSVPRepository
- **Services**: StudySessionService, SessionRSVPService
- **Controllers**: StudySessionController, SessionRSVPController
- **DTOs**: CreateSessionRequest, UpdateSessionRequest, SessionResponse, RSVPRequest, RSVPResponse

The notification system uses Spring's `@Scheduled` annotation to automatically send reminders at 8:00 AM every day for sessions scheduled the next day. It also provides an endpoint for manually triggering reminders.

## Security

- Creating, updating, and deleting sessions requires ROLE_LEADER or ROLE_ADMIN
- Manually triggering reminders requires ROLE_ADMIN
- Submitting, updating, and deleting RSVPs requires authentication
- Only group members can RSVP to sessions
- Users can only update or delete their own RSVPs