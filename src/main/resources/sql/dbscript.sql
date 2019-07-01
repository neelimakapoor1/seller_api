
drop table if exists notification_registry;
drop table if exists product_registry;
drop table if exists product_rank;
drop table if exists product;
CREATE TABLE public.product
(
   product_id serial,
   asin_id character varying(100),
   email1 text,
   email2 text,
   refresh_interval integer default 24,
   receive_notifications boolean default false,
   notification_interval integer default 24,
   added_by character varying,
   added_date timestamp without time zone DEFAULT now(),
   CONSTRAINT product_pkey PRIMARY KEY (product_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

drop table if exists product_details;
CREATE TABLE public.product_detail
(
   detail_id serial,
   product_id integer,
   details text,
   added_date timestamp without time zone DEFAULT now(),
   CONSTRAINT product_detail_pkey PRIMARY KEY (detail_id),
   CONSTRAINT product_detail_product_id_fk FOREIGN KEY (product_id)
        REFERENCES public.product (product_id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;


drop table if exists product_keyword;
CREATE TABLE public.product_keyword
(
   keyword_id serial,
   product_id integer,
   keyword text,
   added_date timestamp without time zone DEFAULT now(),
   CONSTRAINT product_keyword_pkey PRIMARY KEY (keyword_id),
   CONSTRAINT product_keyword_product_id_fk FOREIGN KEY (product_id)
        REFERENCES public.product (product_id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

drop table if exists keyword_rank;
CREATE TABLE public.keyword_rank
(
   rank_id serial,
   keyword_id integer,
   rank integer,
   paid_rank integer,
   added_date timestamp without time zone DEFAULT now(),
   CONSTRAINT keyword_rank_pkey PRIMARY KEY (rank_id),
   CONSTRAINT keyword_rank_keyword_id_fk FOREIGN KEY (keyword_id)
        REFERENCES public.product_keyword (keyword_id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;