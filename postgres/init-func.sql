CREATE OR REPLACE FUNCTION check_release_date()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM Sprint
        WHERE id = NEW.sprint_id
        AND NEW.release_date BETWEEN start_date AND end_date
    ) THEN
        RAISE EXCEPTION 'Release date must be within the sprint dates.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION check_sprint_dates_for_releases()
RETURNS TRIGGER AS $$
DECLARE
    p_release_date DATE;
BEGIN
    FOR p_release_date IN
        SELECT release_date
        FROM Releases
        WHERE sprint_id = NEW.id
    LOOP
        IF p_release_date < NEW.start_date OR p_release_date > NEW.end_date THEN
            RAISE EXCEPTION 'Release date % is outside the new sprint dates.', p_release_date;
        END IF;
    END LOOP;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Получение спринтов по году и команде
CREATE OR REPLACE FUNCTION get_sprints_by_year_and_team(
    target_year INT,
    input_team_name VARCHAR(255) DEFAULT NULL
)
RETURNS TABLE (
    sprintId INT,
    majorVersion VARCHAR(255),
    startDate DATE,
    endDate DATE,
    teamName VARCHAR(255),
    teamColor VARCHAR(7)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        s.id,
        s.major_version,
        s.start_date,
        s.end_date,
        t.name,
        t.color
    FROM
        Sprint s
    JOIN
        Team t ON s.team_id = t.id
    WHERE
        (EXTRACT(YEAR FROM s.start_date) = target_year OR EXTRACT(YEAR FROM s.end_date) = target_year)
        AND (input_team_name IS NULL OR t.name = input_team_name);
END;
$$;

--- Функция для получения топ-10 рисков по задачам
CREATE OR REPLACE FUNCTION get_top_10_task_risks()
RETURNS TABLE (
    riskId INT,
    description TEXT,
    totalEstimatedLoss DECIMAL
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.id,
        r.description,
        SUM(r.estimated_loss) AS total_estimated_loss
    FROM
        Risk r
    JOIN
        Task_Risk tr ON r.id = tr.risk_id
    GROUP BY
        r.id, r.description
    ORDER BY
        total_estimated_loss DESC
    LIMIT 10;
END;
$$;

--- Рассчет текущей загрузки команды на спринт
CREATE OR REPLACE FUNCTION calculate_team_load(input_team_id INT, input_sprint_id INT)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
  team_capacity NUMERIC;
  total_story_points NUMERIC;
BEGIN
  SELECT COUNT(*) 
  INTO team_capacity 
  FROM Users 
  WHERE team_id = input_team_id;

  SELECT SUM(story_points)
  INTO total_story_points
  FROM Task
  WHERE sprint_id = input_sprint_id;

  RETURN (total_story_points / team_capacity);
END;
$$;

--- Получить количество сторипоинтов пользователей за спринт
CREATE OR REPLACE FUNCTION get_story_points_per_user_in_sprint(sprint_id INT)
RETURNS TABLE (
    userLogin VARCHAR(255),
    totalStoryPoints BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
  RETURN QUERY 
  SELECT 
    u.login, 
    COALESCE(SUM(t.story_points), 0) as total_story_points
  FROM Users u
  LEFT JOIN Task t ON u.login = t.implementer AND t.sprint_id = get_story_points_per_user_in_sprint.sprint_id
  WHERE u.team_id = (SELECT team_id FROM Sprint WHERE id = get_story_points_per_user_in_sprint.sprint_id)
  GROUP BY u.login;
END;
$$;