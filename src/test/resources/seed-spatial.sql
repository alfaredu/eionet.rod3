DELETE
FROM T_SPATIAL;
INSERT INTO T_SPATIAL (PK_SPATIAL_ID, SPATIAL_NAME, SPATIAL_TYPE,
                       SPATIAL_TWOLETTER, SPATIAL_ISMEMBERCOUNTRY)
VALUES (1, 'Austria', 'C', 'AT', 'Y'),
       (2, 'Albania', 'C', 'AL', 'N'),
       (3, 'Francia', 'C', 'FR', 'Y');
