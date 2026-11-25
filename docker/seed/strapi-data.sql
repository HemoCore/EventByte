-- Export des données Strapi EventByte (événements, lieux, tickets, image de couverture).
-- ATTENTION : remplace les événements déjà présents dans le Strapi de la personne qui importe.
BEGIN;
SET session_replication_role = replica; -- désactive les FK le temps de l'import (ids admin différents)
TRUNCATE strapi.events, strapi.events_cmps, strapi.components_value_object_tickets,
         strapi.components_value_object_venues, strapi.files, strapi.files_folder_lnk,
         strapi.files_related_mph, strapi.outbox_events RESTART IDENTITY;
--
-- PostgreSQL database dump
--


-- Dumped from database version 15.20 (Debian 15.20-1.pgdg13+1)
-- Dumped by pg_dump version 15.20 (Debian 15.20-1.pgdg13+1)


--
-- Data for Name: components_value_object_tickets; Type: TABLE DATA; Schema: strapi; Owner: -
--

INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (1, 'Standard', 170, 15.41, 'tkt_01a20dce-fc0d-73a3-9154-9e9128649b33');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (2, 'Standard', 9736, 16.35, 'tkt_01a20dce-fc14-76c9-9c74-7d1d39713edd');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (3, 'Premium', 110, 44.33, 'tkt_01a20dce-fc0d-73a3-9154-a3c3707a971a');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (4, 'Standard', 244, 27.18, 'tkt_01a20dce-fc19-7628-b33a-5e1f93b8b803');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (5, 'VIP', 306, 134.77, 'tkt_01a20dce-fc0d-73a3-9154-a653bfdb2839');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (6, 'Premium', 78, 43.42, 'tkt_01a20dce-fc19-7628-b33a-62d57f67ab58');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (7, 'VIP', 196, 136.40, 'tkt_01a20dce-fc19-7628-b33a-676084adc0a4');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (8, 'Standard', 1575, 15.82, 'tkt_01a20dce-fc3a-76f8-a45c-ac226816f05e');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (9, 'Premium', 3249, 41.04, 'tkt_01a20dce-fc3a-76f8-a45c-b2b07292d8cd');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (10, 'Standard', 69, 31.62, 'tkt_01a20dce-fc68-77ef-8670-3dd976c0d3a7');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (11, 'Premium', 191, 77.45, 'tkt_01a20dce-fc68-77ef-8670-4161d032d66d');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (12, 'Standard', 238, 18.77, 'tkt_01a20dce-fd3c-7302-9e0b-db2c370f3388');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (13, 'Premium', 528, 66.16, 'tkt_01a20dce-fd3c-7302-9e0b-dd0b4836c1ee');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (14, 'Standard', 25, 24.57, 'tkt_01a20dce-fd6c-778d-9e38-faf9d567eeff');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (15, 'Premium', 8, 63.34, 'tkt_01a20dce-fd6c-778d-9e38-fdd1eec1f643');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (16, 'VIP', 31, 95.02, 'tkt_01a20dce-fd6c-778d-9e39-01d432a9f1bf');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (17, 'Standard', 348, 30.19, 'tkt_01a20dce-fd91-7228-8775-09ec4afe430e');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (18, 'Standard', 2935, 27.30, 'tkt_01a20dce-fd9e-702c-abf3-1996058aa8c6');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (19, 'Premium', 10648, 61.58, 'tkt_01a20dce-fd9e-702c-abf3-1d1b2fc6329f');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (20, 'Standard', 21, 27.51, 'tkt_01a20dce-fdd2-774f-bcbf-ceac42c054fb');
INSERT INTO strapi.components_value_object_tickets (id, name, quantity, price, uuid) VALUES (21, 'Premium', 78, 43.39, 'tkt_01a20dce-fdd2-774f-bcbf-d373e8cc0e07');


--
-- Data for Name: components_value_object_venues; Type: TABLE DATA; Schema: strapi; Owner: -
--

INSERT INTO strapi.components_value_object_venues (id, name, adress, coordinate) VALUES (1, 'Salle Hilpert', '55 Rue de la Liberté, 34000 Montpellier', '{"lat": 43.586111974125885, "lng": 3.9152836441612755}');
INSERT INTO strapi.components_value_object_venues (id, name, adress, coordinate) VALUES (2, 'Arena Lehner', '71 Impasse Foch, 59000 Lille', '{"lat": 50.61670664724536, "lng": 3.0963504401291355}');
INSERT INTO strapi.components_value_object_venues (id, name, adress, coordinate) VALUES (3, 'Chapiteau Hahn', '90 Impasse de la République, 75000 Paris', '{"lat": 48.8242999059373, "lng": 2.3053990092892183}');
INSERT INTO strapi.components_value_object_venues (id, name, adress, coordinate) VALUES (4, 'Centre-ville Kunze', '18 Place des Écoles, 75000 Paris', '{"lat": 48.890702594622, "lng": 2.2415475655753037}');
INSERT INTO strapi.components_value_object_venues (id, name, adress, coordinate) VALUES (5, 'Salle Watsica', '46 Avenue Jean Jaurès, 33000 Bordeaux', '{"lat": 44.829465822399584, "lng": -0.5358636178708691}');
INSERT INTO strapi.components_value_object_venues (id, name, adress, coordinate) VALUES (6, 'Salle Jacobi', '89 Impasse Saint-Michel, 49000 Angers', '{"lat": 47.45066943676208, "lng": -0.5564171737623801}');
INSERT INTO strapi.components_value_object_venues (id, name, adress, coordinate) VALUES (7, 'Club Funk', '97 Impasse Victor Hugo, 59000 Lille', '{"lat": 50.62498836695294, "lng": 3.0892496808368013}');
INSERT INTO strapi.components_value_object_venues (id, name, adress, coordinate) VALUES (8, 'Château Hilpert', '96 Place de la Liberté, 30000 Nîmes', '{"lat": 43.822209843624954, "lng": 4.377613000808737}');
INSERT INTO strapi.components_value_object_venues (id, name, adress, coordinate) VALUES (9, 'Circuit Larson', '72 Place Victor Hugo, 67000 Strasbourg', '{"lat": 48.55563658002008, "lng": 7.728375542707149}');
INSERT INTO strapi.components_value_object_venues (id, name, adress, coordinate) VALUES (10, 'Restaurant Prosacco', '111 Place Voltaire, 31000 Toulouse', '{"lat": 43.62645575479355, "lng": 1.4556010325212152}');


--
-- Data for Name: events; Type: TABLE DATA; Schema: strapi; Owner: -
--

INSERT INTO strapi.events (id, document_id, uuid, title, description, start_at, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (1, 'ycycxvnw54cf7umrq4hgb60x', 'evt_01a20dce-fc3c-719f-aaca-fe2415b921fe', 'Festival de Musique Électronique', 'Festival en plein air avec les meilleurs DJs', '2026-12-29 17:00:00', '2026-11-24 13:46:38.524', '2026-11-24 13:46:38.524', '2026-11-24 13:46:38.405', NULL, NULL, NULL);
INSERT INTO strapi.events (id, document_id, uuid, title, description, start_at, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (2, 'w7349p03fg4dsrjqtyj9kxs6', 'evt_01a20dce-fc48-70db-97dd-56bac7b81173', 'Spectacle pour Enfants En Première', 'Divertissement familial avec animations et surprises', '2027-04-10 17:00:00', '2026-11-24 13:46:38.536', '2026-11-24 13:46:38.536', '2026-11-24 13:46:38.381', NULL, NULL, NULL);
INSERT INTO strapi.events (id, document_id, uuid, title, description, start_at, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (3, 'i65w6vqyweyl8lgn5zvlpxpe', 'evt_01a20dce-fc51-710b-a781-8f79925a18d1', 'Spectacle de Cirque', 'Acrobaties et performances époustouflantes pour toute la famille', '2027-03-19 19:00:00', '2026-11-24 13:46:38.545', '2026-11-24 13:46:38.545', '2026-11-24 13:46:38.447', NULL, NULL, NULL);
INSERT INTO strapi.events (id, document_id, uuid, title, description, start_at, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (4, 'mghpa1qw3y2hk560zqx20u3b', 'evt_01a20dce-fc5a-7400-84cd-cfdb80847a1f', 'Carnaval de Rue', 'Défilé coloré avec chars et animations pour tous', '2027-01-22 14:00:00', '2026-11-24 13:46:38.554', '2026-11-24 13:46:38.554', '2026-11-24 13:46:38.491', NULL, NULL, NULL);
INSERT INTO strapi.events (id, document_id, uuid, title, description, start_at, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (5, 'qlvxk8iufa8gbnq4whmhg5l9', 'evt_01a20dce-fc8d-72b1-adea-1ab0c4094eb9', 'Concert de Pop avec The Maestro', 'Les hits du moment en live avec vos artistes préférés', '2027-02-20 19:00:00', '2026-11-24 13:46:38.605', '2026-11-24 13:46:38.605', '2026-11-24 13:46:38.529', NULL, NULL, NULL);
INSERT INTO strapi.events (id, document_id, uuid, title, description, start_at, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (6, 'mkt9hbvfn2ogbl72flq0s2c2', 'evt_01a20dce-fd7e-72f4-8951-1a1ad478c278', 'Soirée Karaoké Géant - 1ère édition', 'Venez chanter vos chansons préférées dans une ambiance conviviale', '2026-12-15 20:00:00', '2026-11-24 13:46:38.846', '2026-11-24 13:46:38.846', '2026-11-24 13:46:38.731', NULL, NULL, NULL);
INSERT INTO strapi.events (id, document_id, uuid, title, description, start_at, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (7, 'rpbzegxdz3514mbs1hd9d91h', 'evt_01a20dce-fdab-7386-89b0-b594a2b8b37b', 'Fête Médiévale 2026', 'Plongez dans l''univers du Moyen Âge avec tournois et banquet', '2027-02-01 20:00:00', '2026-11-24 13:46:38.891', '2026-11-24 13:46:38.891', '2026-11-24 13:46:38.804', NULL, NULL, NULL);
INSERT INTO strapi.events (id, document_id, uuid, title, description, start_at, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (8, 'e6bbwlio1rpl9jaor7k1s33m', 'evt_01a20dce-fdd5-75b8-9f0f-f67eb71e7c0a', 'Concert de Jazz', 'Une soirée exceptionnelle de jazz avec des artistes internationaux', '2027-04-14 06:00:00', '2026-11-24 13:46:38.933', '2026-11-24 13:46:38.933', '2026-11-24 13:46:38.782', NULL, NULL, NULL);
INSERT INTO strapi.events (id, document_id, uuid, title, description, start_at, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (9, 'x8ikot38v48qj2nuulxxamci', 'evt_01a20dce-fddc-72eb-ad43-ec81d9cc17c0', 'Course de Cyclisme En Première', 'Épreuve cycliste avec parcours spectaculaire', '2027-04-19 18:00:00', '2026-11-24 13:46:38.94', '2026-11-24 13:46:38.94', '2026-11-24 13:46:38.831', NULL, NULL, NULL);
INSERT INTO strapi.events (id, document_id, uuid, title, description, start_at, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (10, 'e5lxxmui0dqd2vq9jzynjfv3', 'evt_01a20dce-fdec-748e-b883-754efb13d6a9', 'Brunch Musical', 'Brunch gourmand accompagné de musique live', '2027-02-28 22:00:00', '2026-11-24 13:46:38.956', '2026-11-24 13:46:38.956', '2026-11-24 13:46:38.867', NULL, NULL, NULL);


--
-- Data for Name: events_cmps; Type: TABLE DATA; Schema: strapi; Owner: -
--

INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (1, 2, 1, 'value-object.venue', 'venue', NULL);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (2, 1, 2, 'value-object.venue', 'venue', NULL);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (3, 2, 1, 'value-object.ticket', 'tickets', 1);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (4, 2, 3, 'value-object.ticket', 'tickets', 2);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (5, 2, 5, 'value-object.ticket', 'tickets', 3);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (6, 3, 3, 'value-object.venue', 'venue', NULL);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (7, 1, 2, 'value-object.ticket', 'tickets', 1);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (8, 3, 4, 'value-object.ticket', 'tickets', 1);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (9, 3, 6, 'value-object.ticket', 'tickets', 2);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (10, 3, 7, 'value-object.ticket', 'tickets', 3);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (11, 4, 4, 'value-object.venue', 'venue', NULL);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (12, 4, 8, 'value-object.ticket', 'tickets', 1);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (13, 4, 9, 'value-object.ticket', 'tickets', 2);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (14, 5, 5, 'value-object.venue', 'venue', NULL);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (15, 5, 10, 'value-object.ticket', 'tickets', 1);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (16, 5, 11, 'value-object.ticket', 'tickets', 2);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (17, 6, 6, 'value-object.venue', 'venue', NULL);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (18, 6, 12, 'value-object.ticket', 'tickets', 1);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (19, 6, 13, 'value-object.ticket', 'tickets', 2);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (20, 8, 7, 'value-object.venue', 'venue', NULL);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (21, 7, 8, 'value-object.venue', 'venue', NULL);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (22, 8, 14, 'value-object.ticket', 'tickets', 1);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (23, 8, 15, 'value-object.ticket', 'tickets', 2);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (24, 8, 16, 'value-object.ticket', 'tickets', 3);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (25, 9, 9, 'value-object.venue', 'venue', NULL);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (26, 7, 17, 'value-object.ticket', 'tickets', 1);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (27, 9, 18, 'value-object.ticket', 'tickets', 1);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (28, 9, 19, 'value-object.ticket', 'tickets', 2);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (29, 10, 10, 'value-object.venue', 'venue', NULL);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (30, 10, 20, 'value-object.ticket', 'tickets', 1);
INSERT INTO strapi.events_cmps (id, entity_id, cmp_id, component_type, field, "order") VALUES (31, 10, 21, 'value-object.ticket', 'tickets', 2);


--
-- Data for Name: files; Type: TABLE DATA; Schema: strapi; Owner: -
--

INSERT INTO strapi.files (id, document_id, name, alternative_text, caption, width, height, formats, hash, ext, mime, size, url, preview_url, provider, provider_metadata, folder_path, created_at, updated_at, published_at, created_by_id, updated_by_id, locale) VALUES (1, 'ome1jcy444b5qub70vthdq7c', 'aa.jpg', NULL, NULL, 800, 576, '{"small": {"ext": ".jpg", "url": "https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/small_aa_1d5b97abb8.jpg", "hash": "small_aa_1d5b97abb8", "mime": "image/jpeg", "name": "small_aa.jpg", "path": null, "size": 21.05, "width": 500, "height": 360, "sizeInBytes": 21052, "provider_metadata": {"public_id": "small_aa_1d5b97abb8", "resource_type": "image"}}, "medium": {"ext": ".jpg", "url": "https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/medium_aa_1d5b97abb8.jpg", "hash": "medium_aa_1d5b97abb8", "mime": "image/jpeg", "name": "medium_aa.jpg", "path": null, "size": 42.57, "width": 750, "height": 540, "sizeInBytes": 42568, "provider_metadata": {"public_id": "medium_aa_1d5b97abb8", "resource_type": "image"}}, "thumbnail": {"ext": ".jpg", "url": "https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/thumbnail_aa_1d5b97abb8.jpg", "hash": "thumbnail_aa_1d5b97abb8", "mime": "image/jpeg", "name": "thumbnail_aa.jpg", "path": null, "size": 5.25, "width": 217, "height": 156, "sizeInBytes": 5254, "provider_metadata": {"public_id": "thumbnail_aa_1d5b97abb8", "resource_type": "image"}}}', 'aa_1d5b97abb8', '.jpg', 'image/jpeg', 44.45, 'https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg', NULL, 'cloudinary', '{"public_id": "aa_1d5b97abb8", "resource_type": "image"}', '/', '2026-11-24 10:01:32.325', '2026-11-24 10:01:32.325', '2026-11-24 10:01:32.326', 1, 1, NULL);


--
-- Data for Name: files_folder_lnk; Type: TABLE DATA; Schema: strapi; Owner: -
--



--
-- Data for Name: files_related_mph; Type: TABLE DATA; Schema: strapi; Owner: -
--

INSERT INTO strapi.files_related_mph (id, file_id, related_id, related_type, field, "order") VALUES (1, 1, 1, 'api::event.event', 'cover', 1);
INSERT INTO strapi.files_related_mph (id, file_id, related_id, related_type, field, "order") VALUES (2, 1, 2, 'api::event.event', 'cover', 1);
INSERT INTO strapi.files_related_mph (id, file_id, related_id, related_type, field, "order") VALUES (3, 1, 3, 'api::event.event', 'cover', 1);
INSERT INTO strapi.files_related_mph (id, file_id, related_id, related_type, field, "order") VALUES (4, 1, 4, 'api::event.event', 'cover', 1);
INSERT INTO strapi.files_related_mph (id, file_id, related_id, related_type, field, "order") VALUES (5, 1, 5, 'api::event.event', 'cover', 1);
INSERT INTO strapi.files_related_mph (id, file_id, related_id, related_type, field, "order") VALUES (6, 1, 6, 'api::event.event', 'cover', 1);
INSERT INTO strapi.files_related_mph (id, file_id, related_id, related_type, field, "order") VALUES (7, 1, 7, 'api::event.event', 'cover', 1);
INSERT INTO strapi.files_related_mph (id, file_id, related_id, related_type, field, "order") VALUES (8, 1, 8, 'api::event.event', 'cover', 1);
INSERT INTO strapi.files_related_mph (id, file_id, related_id, related_type, field, "order") VALUES (9, 1, 9, 'api::event.event', 'cover', 1);
INSERT INTO strapi.files_related_mph (id, file_id, related_id, related_type, field, "order") VALUES (10, 1, 10, 'api::event.event', 'cover', 1);


--
-- Data for Name: outbox_events; Type: TABLE DATA; Schema: strapi; Owner: -
--

INSERT INTO strapi.outbox_events (id, event_type, payload, processed, created_at, processed_at) VALUES (1, 'event.created', '{"id":"evt_01a20dce-fc48-70db-97dd-56bac7b81173","title":"Spectacle pour Enfants En Première","description":"Divertissement familial avec animations et surprises","cover":"https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg","venue":{"name":"Salle Hilpert","address":"55 Rue de la Liberté, 34000 Montpellier","latitude":43.586111974125885,"longitude":3.9152836441612755},"tickets":[{"id":"tkt_01a20dce-fc0d-73a3-9154-9e9128649b33","name":"Standard","quantity":170,"price":15.41},{"id":"tkt_01a20dce-fc0d-73a3-9154-a3c3707a971a","name":"Premium","quantity":110,"price":44.33},{"id":"tkt_01a20dce-fc0d-73a3-9154-a653bfdb2839","name":"VIP","quantity":306,"price":134.77}]}', false, '2026-11-24 13:46:38.315857+00', '2026-11-24 13:46:39.039+00');
INSERT INTO strapi.outbox_events (id, event_type, payload, processed, created_at, processed_at) VALUES (3, 'event.created', '{"id":"evt_01a20dce-fc3c-719f-aaca-fe2415b921fe","title":"Festival de Musique Électronique","description":"Festival en plein air avec les meilleurs DJs","cover":"https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg","venue":{"name":"Arena Lehner","address":"71 Impasse Foch, 59000 Lille","latitude":50.61670664724536,"longitude":3.0963504401291355},"tickets":[{"id":"tkt_01a20dce-fc14-76c9-9c74-7d1d39713edd","name":"Standard","quantity":9736,"price":16.35}]}', false, '2026-11-24 13:46:38.323442+00', '2026-11-24 13:46:39.054+00');
INSERT INTO strapi.outbox_events (id, event_type, payload, processed, created_at, processed_at) VALUES (9, 'event.created', '{"id":"evt_01a20dce-fdec-748e-b883-754efb13d6a9","title":"Brunch Musical","description":"Brunch gourmand accompagné de musique live","cover":"https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg","venue":{"name":"Restaurant Prosacco","address":"111 Place Voltaire, 31000 Toulouse","latitude":43.62645575479355,"longitude":1.4556010325212152},"tickets":[{"id":"tkt_01a20dce-fdd2-774f-bcbf-ceac42c054fb","name":"Standard","quantity":21,"price":27.51},{"id":"tkt_01a20dce-fdd2-774f-bcbf-d373e8cc0e07","name":"Premium","quantity":78,"price":43.39}]}', false, '2026-11-24 13:46:38.85807+00', '2026-11-24 13:46:40.006+00');
INSERT INTO strapi.outbox_events (id, event_type, payload, processed, created_at, processed_at) VALUES (10, 'event.created', '{"id":"evt_01a20dce-fdab-7386-89b0-b594a2b8b37b","title":"Fête Médiévale 2026","description":"Plongez dans l''univers du Moyen Âge avec tournois et banquet","cover":"https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg","venue":{"name":"Château Hilpert","address":"96 Place de la Liberté, 30000 Nîmes","latitude":43.822209843624954,"longitude":4.377613000808737},"tickets":[{"id":"tkt_01a20dce-fd91-7228-8775-09ec4afe430e","name":"Standard","quantity":348,"price":30.19}]}', false, '2026-11-24 13:46:38.792915+00', '2026-11-24 13:46:40.008+00');
INSERT INTO strapi.outbox_events (id, event_type, payload, processed, created_at, processed_at) VALUES (2, 'event.created', '{"id":"evt_01a20dce-fc51-710b-a781-8f79925a18d1","title":"Spectacle de Cirque","description":"Acrobaties et performances époustouflantes pour toute la famille","cover":"https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg","venue":{"name":"Chapiteau Hahn","address":"90 Impasse de la République, 75000 Paris","latitude":48.8242999059373,"longitude":2.3053990092892183},"tickets":[{"id":"tkt_01a20dce-fc19-7628-b33a-5e1f93b8b803","name":"Standard","quantity":244,"price":27.18},{"id":"tkt_01a20dce-fc19-7628-b33a-62d57f67ab58","name":"Premium","quantity":78,"price":43.42},{"id":"tkt_01a20dce-fc19-7628-b33a-676084adc0a4","name":"VIP","quantity":196,"price":136.4}]}', false, '2026-11-24 13:46:38.404719+00', '2026-11-24 13:46:39.046+00');
INSERT INTO strapi.outbox_events (id, event_type, payload, processed, created_at, processed_at) VALUES (4, 'event.created', '{"id":"evt_01a20dce-fc5a-7400-84cd-cfdb80847a1f","title":"Carnaval de Rue","description":"Défilé coloré avec chars et animations pour tous","cover":"https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg","venue":{"name":"Centre-ville Kunze","address":"18 Place des Écoles, 75000 Paris","latitude":48.890702594622,"longitude":2.2415475655753037},"tickets":[{"id":"tkt_01a20dce-fc3a-76f8-a45c-ac226816f05e","name":"Standard","quantity":1575,"price":15.82},{"id":"tkt_01a20dce-fc3a-76f8-a45c-b2b07292d8cd","name":"Premium","quantity":3249,"price":41.04}]}', false, '2026-11-24 13:46:38.483656+00', '2026-11-24 13:46:39.061+00');
INSERT INTO strapi.outbox_events (id, event_type, payload, processed, created_at, processed_at) VALUES (5, 'event.created', '{"id":"evt_01a20dce-fc8d-72b1-adea-1ab0c4094eb9","title":"Concert de Pop avec The Maestro","description":"Les hits du moment en live avec vos artistes préférés","cover":"https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg","venue":{"name":"Salle Watsica","address":"46 Avenue Jean Jaurès, 33000 Bordeaux","latitude":44.829465822399584,"longitude":-0.5358636178708691},"tickets":[{"id":"tkt_01a20dce-fc68-77ef-8670-3dd976c0d3a7","name":"Standard","quantity":69,"price":31.62},{"id":"tkt_01a20dce-fc68-77ef-8670-4161d032d66d","name":"Premium","quantity":191,"price":77.45}]}', false, '2026-11-24 13:46:38.518716+00', '2026-11-24 13:46:39.066+00');
INSERT INTO strapi.outbox_events (id, event_type, payload, processed, created_at, processed_at) VALUES (6, 'event.created', '{"id":"evt_01a20dce-fd7e-72f4-8951-1a1ad478c278","title":"Soirée Karaoké Géant - 1ère édition","description":"Venez chanter vos chansons préférées dans une ambiance conviviale","cover":"https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg","venue":{"name":"Salle Jacobi","address":"89 Impasse Saint-Michel, 49000 Angers","latitude":47.45066943676208,"longitude":-0.5564171737623801},"tickets":[{"id":"tkt_01a20dce-fd3c-7302-9e0b-db2c370f3388","name":"Standard","quantity":238,"price":18.77},{"id":"tkt_01a20dce-fd3c-7302-9e0b-dd0b4836c1ee","name":"Premium","quantity":528,"price":66.16}]}', false, '2026-11-24 13:46:38.719549+00', '2026-11-24 13:46:39.07+00');
INSERT INTO strapi.outbox_events (id, event_type, payload, processed, created_at, processed_at) VALUES (7, 'event.created', '{"id":"evt_01a20dce-fdd5-75b8-9f0f-f67eb71e7c0a","title":"Concert de Jazz","description":"Une soirée exceptionnelle de jazz avec des artistes internationaux","cover":"https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg","venue":{"name":"Club Funk","address":"97 Impasse Victor Hugo, 59000 Lille","latitude":50.62498836695294,"longitude":3.0892496808368013},"tickets":[{"id":"tkt_01a20dce-fd6c-778d-9e38-faf9d567eeff","name":"Standard","quantity":25,"price":24.57},{"id":"tkt_01a20dce-fd6c-778d-9e38-fdd1eec1f643","name":"Premium","quantity":8,"price":63.34},{"id":"tkt_01a20dce-fd6c-778d-9e39-01d432a9f1bf","name":"VIP","quantity":31,"price":95.02}]}', false, '2026-11-24 13:46:38.769375+00', '2026-11-24 13:46:39.074+00');
INSERT INTO strapi.outbox_events (id, event_type, payload, processed, created_at, processed_at) VALUES (8, 'event.created', '{"id":"evt_01a20dce-fddc-72eb-ad43-ec81d9cc17c0","title":"Course de Cyclisme En Première","description":"Épreuve cycliste avec parcours spectaculaire","cover":"https://res.cloudinary.com/ajfsnai4/image/upload/v1795514490/aa_1d5b97abb8.jpg","venue":{"name":"Circuit Larson","address":"72 Place Victor Hugo, 67000 Strasbourg","latitude":48.55563658002008,"longitude":7.728375542707149},"tickets":[{"id":"tkt_01a20dce-fd9e-702c-abf3-1996058aa8c6","name":"Standard","quantity":2935,"price":27.3},{"id":"tkt_01a20dce-fd9e-702c-abf3-1d1b2fc6329f","name":"Premium","quantity":10648,"price":61.58}]}', false, '2026-11-24 13:46:38.819365+00', '2026-11-24 13:46:39.079+00');


--
-- Name: components_value_object_tickets_id_seq; Type: SEQUENCE SET; Schema: strapi; Owner: -
--

SELECT pg_catalog.setval('strapi.components_value_object_tickets_id_seq', 21, true);


--
-- Name: components_value_object_venues_id_seq; Type: SEQUENCE SET; Schema: strapi; Owner: -
--

SELECT pg_catalog.setval('strapi.components_value_object_venues_id_seq', 10, true);


--
-- Name: events_cmps_id_seq; Type: SEQUENCE SET; Schema: strapi; Owner: -
--

SELECT pg_catalog.setval('strapi.events_cmps_id_seq', 31, true);


--
-- Name: events_id_seq; Type: SEQUENCE SET; Schema: strapi; Owner: -
--

SELECT pg_catalog.setval('strapi.events_id_seq', 10, true);


--
-- Name: files_folder_lnk_id_seq; Type: SEQUENCE SET; Schema: strapi; Owner: -
--

SELECT pg_catalog.setval('strapi.files_folder_lnk_id_seq', 1, false);


--
-- Name: files_id_seq; Type: SEQUENCE SET; Schema: strapi; Owner: -
--

SELECT pg_catalog.setval('strapi.files_id_seq', 1, true);


--
-- Name: files_related_mph_id_seq; Type: SEQUENCE SET; Schema: strapi; Owner: -
--

SELECT pg_catalog.setval('strapi.files_related_mph_id_seq', 10, true);


--
-- Name: outbox_events_id_seq; Type: SEQUENCE SET; Schema: strapi; Owner: -
--

SELECT pg_catalog.setval('strapi.outbox_events_id_seq', 10, true);


--
-- PostgreSQL database dump complete
--


-- L'auteur admin n'existe pas forcément chez l'autre personne
UPDATE strapi.events SET created_by_id = NULL, updated_by_id = NULL;
UPDATE strapi.files  SET created_by_id = NULL, updated_by_id = NULL;
UPDATE strapi.outbox_events o
SET payload = (o.payload::jsonb || jsonb_build_object(
        'startAt', to_char(e.start_at AT TIME ZONE 'UTC', 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')))::json
FROM strapi.events e
WHERE e.uuid = o.payload->>'id';

-- Strapi republiera les messages event.created sur le RabbitMQ local
UPDATE strapi.outbox_events SET processed = false, processed_at = NULL;
SET session_replication_role = DEFAULT;
COMMIT;
