DELETE
FROM T_ISSUE;
INSERT INTO T_ISSUE (PK_ISSUE_ID, ISSUE_NAME)
VALUES (1, 'Climate Change'),
       (2, 'Ozone Depletion');
DELETE
FROM T_RAISSUE_LNK;
INSERT INTO T_RAISSUE_LNK (FK_ISSUE_ID, FK_RA_ID)
VALUES (1, 1),
       (1, 2);
