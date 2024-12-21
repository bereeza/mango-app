SET search_path TO mango;

--trigger for vacancy statistic (create operation);
CREATE OR REPLACE FUNCTION create_statistic()
    RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO mango.vacancy_statistic (vacancy_id, views, applicants)
    VALUES (NEW.id, 0, 0);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_vacancy_create
    AFTER INSERT ON mango.vacancy
    FOR EACH ROW
EXECUTE FUNCTION create_statistic();

--trigger for vacancy statistic (drop operation);
CREATE OR REPLACE FUNCTION drop_statistic()
    RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM mango.vacancy_statistic WHERE vacancy_id = OLD.id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_vacancy_drop
    AFTER DELETE ON mango.vacancy
    FOR EACH ROW
EXECUTE FUNCTION drop_statistic();

