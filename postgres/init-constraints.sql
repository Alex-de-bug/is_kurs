ALTER TABLE Sprint
ADD CONSTRAINT sprint_dates_check CHECK (end_date >= start_date AND regression_end >= regression_start);

ALTER TABLE Task
ADD CONSTRAINT story_points_nonnegative CHECK (story_points >= 0);

ALTER TABLE Risk
ADD CONSTRAINT probability_valid_range CHECK (probability >= 0 AND probability <= 1);

ALTER TABLE Risk
ADD CONSTRAINT estimated_loss_nonnegative CHECK (estimated_loss >= 0);