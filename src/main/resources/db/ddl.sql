create table attachment
(
    id        bigint auto_increment
        primary key,
    file_type varchar(255) not null,
    file_url  varchar(255) not null
);

create table author
(
    deleted_at datetime(6)  null,
    id         bigint auto_increment
        primary key,
    name       varchar(255) not null
);

create table cart
(
    id bigint not null
        primary key
);

create table category
(
    depth     int          not null,
    id        bigint auto_increment
        primary key,
    parent_id bigint       null,
    name      varchar(255) not null,
    constraint FK2y94svpmqttx80mshyny85wqr
        foreign key (parent_id) references category (id)
);

create table delivery
(
    id              bigint auto_increment
        primary key,
    address         varchar(255) not null,
    address_detail  varchar(255) not null,
    company         varchar(255) not null,
    order_id        varchar(255) null,
    phone_number    varchar(255) not null,
    receiver        varchar(255) not null,
    tracking_number varchar(255) not null,
    zipcode         varchar(255) not null,
    order_item_id   bigint       null,
    constraint UK3bdrbd2jcybaaa5rxkj4s7vlk
        unique (order_id)
);

create table member
(
    id           bigint auto_increment
        primary key,
    email        varchar(100)                                 not null,
    name         varchar(100)                                 not null,
    password     varchar(100)                                 not null,
    member_grade enum ('BRONZE', 'DIAMOND', 'GOLD', 'SILVER') not null,
    role         enum ('ROLE_ADMIN', 'ROLE_MEMBER')           not null,
    constraint UKmbmcqelty0fbrvxp1q58dn57t
        unique (email)
);

create table delivery_address
(
    id             bigint auto_increment
        primary key,
    address_detail varchar(500) not null,
    address        varchar(255) not null,
    phone_number   varchar(255) not null,
    receiver       varchar(255) not null,
    zipcode        varchar(255) not null,
    is_default     bit          not null,
    member_id      bigint       null,
    constraint FK7txh3nxg2wpt4lmsgalnxpcm5
        foreign key (member_id) references member (id)
);

create table member_address
(
    is_default          bit    not null,
    delivery_address_id bigint null,
    id                  bigint auto_increment
        primary key,
    member_id           bigint null,
    constraint FK6ecy3echw4x1kqqvlwjjn2o2s
        foreign key (delivery_address_id) references delivery_address (id),
    constraint FKeslc8586cwl3ej73mv7gr83x2
        foreign key (member_id) references member (id)
);

create table order_delivery
(
    id              bigint auto_increment
        primary key,
    company         varchar(255) not null,
    tracking_number varchar(255) not null,
    order_item_id   bigint       null,
    constraint UK2y1cd14sy28825873jijan7mr
        unique (order_item_id)
);

create table orders
(
    payment_amount decimal(38, 2) null,
    used_point     int            null,
    current_pi_id  bigint         null,
    delivery_id    bigint         null,
    member_id      bigint         null,
    ordered_at     datetime(6)    not null,
    address        varchar(255)   not null,
    address_detail varchar(255)   not null,
    id             varchar(255)   not null
        primary key,
    phone_number   varchar(255)   not null,
    receiver       varchar(255)   not null,
    zipcode        varchar(255)   not null,
    constraint UK9ct0l8xfeaiqruabcqjh1neui
        unique (delivery_id),
    constraint UKikj9868q38ttf7fxq9oxw0hgs
        unique (current_pi_id),
    constraint FKpktxwhj3x9m4gth5ff6bkqgeb
        foreign key (member_id) references member (id),
    constraint FKtkrur7wg4d8ax0pwgo0vmy20c
        foreign key (delivery_id) references delivery (id)
);

alter table delivery
    add constraint FKu4e8rjwmg09vmas3ccjwglso
        foreign key (order_id) references orders (id);

create table payment
(
    amount       decimal(38, 2)             not null,
    approved_at  datetime(6)                null,
    cancelled_at datetime(6)                null,
    id           bigint auto_increment
        primary key,
    requested_at datetime(6)                not null,
    payment_key  varchar(200)               not null,
    order_id     varchar(255)               null,
    type         enum ('CANCEL', 'CONFIRM') not null,
    constraint FKlouu98csyullos9k25tbpk4va
        foreign key (order_id) references orders (id)
);

create table payment_intent
(
    amount     decimal(38, 2)                                                                              null,
    created_at datetime(6)                                                                                 not null,
    id         bigint auto_increment
        primary key,
    order_id   varchar(255)                                                                                null,
    status     enum ('CANCELED', 'DONE', 'FAILED', 'PROCESSING', 'READY', 'REQUIRES_PAYMENT', 'SUCCEEDED') null,
    constraint FKfwfrqj2v4698lvl8iv4h5j6gy
        foreign key (order_id) references orders (id)
);

alter table orders
    add constraint FKt8q67prleh5wivb6q9wx56f8f
        foreign key (current_pi_id) references payment_intent (id);

create table payment_transaction
(
    created_at        datetime(6)                                            null,
    id                bigint auto_increment
        primary key,
    payment_intent_id bigint                                                 null,
    error_code        varchar(255)                                           null,
    error_message     varchar(255)                                           null,
    raw_response      varchar(255)                                           null,
    status            enum ('FAILED', 'PENDING', 'PROGRESSING', 'SUCCEEDED') null,
    constraint FKdi6u4sooo821wmo7lkjwee7vp
        foreign key (payment_intent_id) references payment_intent (id)
);

create table product
(
    price_sales decimal(10, 2)                                    not null,
    stock       int                                               not null,
    category_id bigint                                            null,
    id          bigint auto_increment
        primary key,
    view_count  bigint                                            not null,
    image_url   varchar(255)                                      not null,
    name        varchar(255)                                      not null,
    status      enum ('HIDED', 'SELLING', 'SOLD_OUT')             not null,
    type        enum ('BOOK', 'DVD', 'EBOOK', 'FOREIGN', 'MUSIC') not null,
    constraint FK1mtsbur82frn64de7balymq9s
        foreign key (category_id) references category (id)
);

create table cart_item
(
    quantity   int    not null,
    cart_id    bigint null,
    id         bigint auto_increment
        primary key,
    product_id bigint null,
    constraint FK1uobyhgl1wvgt1jpccia8xxs3
        foreign key (cart_id) references cart (id),
    constraint FKjcyd5wv4igqnw413rgxbfu4nv
        foreign key (product_id) references product (id)
);

create table order_item
(
    discount_amount decimal(38, 2)                                                                                                                                                       not null,
    price           decimal(38, 2)                                                                                                                                                       not null,
    quantity        int                                                                                                                                                                  not null,
    id              bigint auto_increment
        primary key,
    product_id      bigint                                                                                                                                                               null,
    order_id        varchar(255)                                                                                                                                                         null,
    status          enum ('DELIVERED', 'DELIVERING', 'ORDER_CANCELLED', 'PAYMENT_COMPLETED', 'PAYMENT_PENDING', 'PURCHASE_CONFIRMED', 'RETURN_COMPLETED', 'RETURN_REQUESTED', 'SHIPPED') not null,
    delivery_id     bigint                                                                                                                                                               null,
    constraint UKs01v9uv1ws0vb6sw6y3uc7gj7
        unique (delivery_id),
    constraint FK551losx9j75ss5d6bfsqvijna
        foreign key (product_id) references product (id),
    constraint FK58egqlvml9lfuihtbxv3ta3vv
        foreign key (delivery_id) references order_delivery (id),
    constraint FKt4dc2r9nbvbujrljv3e23iibt
        foreign key (order_id) references orders (id)
);

alter table delivery
    add constraint FK6jo80w8x5k5fpsrv0d71qr2ug
        foreign key (order_item_id) references order_item (id);

create table order_claim
(
    cancel_amount          decimal(38, 2)                                                                                                                  null,
    completed_at           datetime(6)                                                                                                                     null,
    created_at             datetime(6)                                                                                                                     null,
    id                     bigint auto_increment
        primary key,
    member_id              bigint                                                                                                                          null,
    order_item_id          bigint                                                                                                                          null,
    refunded_at            datetime(6)                                                                                                                     null,
    stock_rolled_back_at   datetime(6)                                                                                                                     null,
    reason                 varchar(255)                                                                                                                    not null,
    return_tracking_number varchar(255)                                                                                                                    null,
    status                 enum ('CANCELED', 'COMPLETED', 'CONFIRMED', 'FAILED', 'IN_PROGRESS', 'REFUNDED', 'REFUND_PROGRESSING', 'REJECTED', 'REQUESTED') not null,
    type                   enum ('CANCEL', 'RETURNS')                                                                                                      not null,
    constraint UKjs49yp8ny37p3fpgvm36a4et6
        unique (order_item_id),
    constraint FK605nn10x3dg189ufpcx6qx4g3
        foreign key (member_id) references member (id),
    constraint FKbhd8m8nh3wv07fobovn4ya6jf
        foreign key (order_item_id) references order_item (id)
);

create table order_claim_item
(
    discount_amount decimal(38, 2)                                                                                   not null,
    quantity        int                                                                                              not null,
    refund_amount   decimal(38, 2)                                                                                   not null,
    unit_price      decimal(38, 2)                                                                                   not null,
    created_at      datetime(6)                                                                                      null,
    id              bigint auto_increment
        primary key,
    order_claim_id  bigint                                                                                           null,
    order_item_id   bigint                                                                                           null,
    reason_detail   varchar(500)                                                                                     null,
    reason          enum ('CHANGE_OF_MIND', 'DEFECTIVE_PRODUCT', 'DELAYED_DELIVERY', 'OTHER', 'WRONG_DELIVERY')      not null,
    status          enum ('APPROVED', 'COLLECTED', 'COLLECTING', 'COMPLETED', 'INSPECTING', 'REJECTED', 'REQUESTED') not null,
    constraint FK5ed77fml3mxfs6mi7f5y9hom9
        foreign key (order_claim_id) references order_claim (id),
    constraint FKjambpg17txnje98ffh0pussh2
        foreign key (order_item_id) references order_item (id)
);

alter table order_delivery
    add constraint FKl8594bex57xkyp3uqm141crwm
        foreign key (order_item_id) references order_item (id);

create table publisher
(
    id   bigint auto_increment
        primary key,
    name varchar(50) not null,
    constraint UKh9trv4xhmh6s68vbw9ba6to70
        unique (name)
);

create table book
(
    price_standard decimal(38, 2) not null,
    publish_date   date           not null,
    id             bigint         not null
        primary key,
    publisher_id   bigint         null,
    isbn13         varchar(255)   not null,
    summary        text           not null,
    title          varchar(255)   not null,
    constraint UKdjx0bsw5qtlpa3ertiyf8j0bc
        unique (isbn13),
    constraint FK8cjf4cjanicu58p2l5t8d9xvu
        foreign key (id) references product (id),
    constraint FKgtvt7p649s4x80y6f4842pnfq
        foreign key (publisher_id) references publisher (id)
);

create table book_author
(
    author_id bigint null,
    book_id   bigint null,
    id        bigint auto_increment
        primary key,
    constraint FKbjqhp85wjv8vpr0beygh6jsgo
        foreign key (author_id) references author (id),
    constraint FKhwgu59n9o80xv75plf9ggj7xn
        foreign key (book_id) references book (id)
);

create table review
(
    star       int          not null,
    created_at datetime(6)  not null,
    id         bigint auto_increment
        primary key,
    member_id  bigint       null,
    content    varchar(255) not null,
    constraint FKk0ccx5i4ci2wd70vegug074w1
        foreign key (member_id) references member (id)
);

create table review_attachment
(
    attachment_id bigint null,
    id            bigint auto_increment
        primary key,
    review_id     bigint null,
    constraint FK54xlvkn2od55vskx76dq2d1b4
        foreign key (review_id) references review (id),
    constraint FKe9kp9kcq0drbds18vxt0kvaqf
        foreign key (attachment_id) references attachment (id)
);

