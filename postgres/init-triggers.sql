CREATE TRIGGER check_release_date_on_insert
BEFORE INSERT ON Releases
FOR EACH ROW
EXECUTE FUNCTION check_release_date();

CREATE TRIGGER check_release_date_on_update
BEFORE UPDATE ON Releases
FOR EACH ROW
WHEN (NEW.release_date <> OLD.release_date OR NEW.sprint_id <> OLD.sprint_id)
EXECUTE FUNCTION check_release_date();

CREATE TRIGGER check_sprint_dates_on_update
BEFORE UPDATE ON Sprint
FOR EACH ROW
EXECUTE FUNCTION check_sprint_dates_for_releases();