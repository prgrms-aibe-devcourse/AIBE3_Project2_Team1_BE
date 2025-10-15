CREATE TABLE project_images
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    image_url  VARCHAR(255)          NULL,
    project_id BIGINT                NULL,
    CONSTRAINT pk_project_images PRIMARY KEY (id)
);

ALTER TABLE project_images
    ADD CONSTRAINT FK_PROJECT_IMAGES_ON_PROJECT FOREIGN KEY (project_id) REFERENCES project (project_id);