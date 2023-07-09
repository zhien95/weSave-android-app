var promise = require("bluebird");
var crypto = require("crypto");
var rand = require("csprng");
var fs = require("fs");
var im = require("imagemagick");

var options = {
  // Initialization Options
  promiseLib: promise
};

var pgp = require("pg-promise")(options);
const db = pgp("postgresql://postgres:123456@localhost:5432/WeSaveDatabase");
// const db = pgp('postgresql://Wesave1234:Wesave1234@wesavedatabase.c6gp1nigs3ft.ap-southeast-1.rds.amazonaws.com:5432/WeSaveDatabase');

db.connect()
  .then(obj => {
    obj.done(); // success, release the connection;
    console.log("Connected to db");
  })
  .catch(error => {
    console.log("ERROR:", error.message || error);
  });

// add query functions
module.exports = {
  //users
  register: register,
  login: login,
  finduser: finduser,
  resetpass: resetpass,
  getuserinfo: getuserinfo,

  //items
  createitem: createitem,
  uploaditemimg: uploaditemimg,
  getitemimage: getitemimage,
  getitem: getitem,
  getallitems: getallitems,
  getfollowingitems: getfollowingitems,
  removefollowingitem: removefollowingitem,
  removelikeitem: removelikeitem,
  getcontributeditems: getcontributeditems,
  getcontributedpricings: getcontributedpricings,
  findbarcode: findbarcode,
  addbarcode: addbarcode,
  getpricing: getpricing,
  sortByDistance: sortByDistance,
  sortByVotes: sortByVotes,
  sortByVotesExpired: sortByVotesExpired,
  sortByPricing: sortByPricing,
  sortByPricingExpired: sortByPricingExpired,
  sortByDate: sortByDate,
  sortByDateExpired: sortByDateExpired,
  like: like,
  view: view,
  follow: follow,
  addprice: addprice,
  getlikecount: getlikecount,
  getviewcount: getviewcount,
  getfeedback: getfeedback,
  pricingfeedback: pricingfeedback,
  updatefeedback: updatefeedback,
  getfollowers: getfollowers,
  getlikers: getlikers,
  getnotifications: getnotifications,
  checkfollow: checkfollow,
  checklike: checklike,
  storenotification: storenotification,
  getcategories: getcategories,
  getsubcategories: getsubcategories,
  getthirdlvlcategories: getthirdlvlcategories,
  getitemsbycategory: getitemsbycategory,
  getcategoryname: getcategoryname,

  //stores
  addstore: addstore,
  getstore: getstore,
  getallstores: getallstores,
  searchNearbyStores: searchNearbyStores,

  //shopping plan
  getminprice: getminprice,
  saveplan: saveplan,
  removeplan: removeplan,
  getshoppingplans: getshoppingplans,

  searchitems: searchitems,
  searchPopularityitems: searchPopularityitems,
  searchNearestitems: searchNearestitems,

  recommendeditems: recommendeditems,
  nearestitems: nearestitems,

  //item comment
  getComments: getComments,
  insertComment: insertComment
};
function getComments(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select * from comments where item_id =" +
      item_id +
      "order by datetime_posted DESC"
  )
    .then(function(data) {
      res.status(200).json({
        status: "success",
        data: data
      });
    })
    .catch(function(err) {
      console.log("Fail to get comment" + err);
      return "Comment failed";
    });
}

function insertComment(req, res) {
  var item_id = req.body.item_id;
  var username = req.body.username;
  var comment = req.body.comment;
  db.none(
    "insert into comments(item_id, username , comment)" + "values($1, $2, $3)",
    [item_id, username, comment]
  )
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "Inserted one comment"
      });
    })
    .catch(function(err) {
      console.log("Fail to insert user" + err);
      return "Comment failed";
    });
}

function register(req, res) {
  console.log("In register");
  var email = req.body.email;
  var password = req.body.password;
  var username = req.body.username;

  var temp = rand(160, 36);
  var newpass = password;
  var token = crypto
    .createHash("sha512")
    .update(email + rand)
    .digest("hex");
  var hashed_password = crypto
    .createHash("sha512")
    .update(newpass)
    .digest("hex");

  db.none(
    "insert into users(token, email, hashed_password, salt, username)" +
      "values($1, $2, $3, $4, $5)",
    [token, email, hashed_password, temp, username]
  )
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "Inserted one user"
      });
      console.log("Successfully inserted");
    })
    .catch(function(err) {
      console.log("Fail to insert user" + err);
      return "Registration failed";
    });
}

function login(req, res) {
  var email = req.body.email;
  var password = req.body.password;

  var hashed_password = crypto
    .createHash("sha512")
    .update(password)
    .digest("hex");

  db.oneOrNone("select * from users where email=$1 and hashed_password=$2", [
    email,
    hashed_password
  ])
    .then(function(data) {
      res.status(200).json({
        status: "success",
        data: data,
        message: "User Found"
      });
    })
    .catch(function(err) {
      return "Error";
      //return (err);
    });
}

function finduser(req, res) {
  var email = req.body.email;
  db.oneOrNone("select * from users where email=$1", email).then(function(
    data
  ) {
    res.status(200).json({
      status: "success",
      data: data
      //message: 'User Found'
    });
  });
}

function resetpass(req, res) {
  var email = req.body.email;
  var password = req.body.password;
  var hashed_password = crypto
    .createHash("sha512")
    .update(password)
    .digest("hex");
  db.none("update users set hashed_password=$1 where email=$2", [
    hashed_password,
    email
  ])
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "Updated password"
      });
    })
    .catch(function(err) {
      return next(err);
    });
}

function createitem(req, res) {
  var name = req.body.name;
  var info = req.body.info;
  var creator_id = req.body.creator_id;
  var third_cat_id = req.body.third_cat_id;
  console.log(name, info, creator_id, third_cat_id);

  db.one(
    "insert into items(name, info, creator_id, third_cat_id) values($1, $2, $3, $4)" +
      " returning id",
    [name, info, creator_id, third_cat_id],
    item => item.id
  )
    .then(function(data) {
      res.status(200).json({
        status: "success",
        data: data.id,
        message: "Inserted one item"
      });
    })
    .catch(function(error) {
      console.log("ERROR:", error.message || error);
    });
}

function addprice(req, res) {
  var item_id = req.body.item_id;
  var store_id = req.body.store_id;
  var original_price = req.body.original_price;
  var promo_price = req.body.promo_price;
  var promo_qty = req.body.promo_qty;
  var creator_id = req.body.creator_id;
  var promo_start = req.body.promo_start;
  var promo_end = req.body.promo_end;
  var has_promo = req.body.has_promo;

  promo_start = new Date(
    Date.UTC(
      promo_start.substring(0, 4),
      promo_start.substring(5, 7) - 1,
      promo_start.substring(8, 10)
    )
  );
  promo_end = new Date(
    Date.UTC(
      promo_end.substring(0, 4),
      promo_end.substring(5, 7) - 1,
      promo_end.substring(8, 10)
    )
  );

  db.none(
    "insert into pricings(item_id, store_id, original_price, promo_price, " +
      "promo_qty, creator_id, promo_start, promo_end, has_promo) values($1, $2, $3, $4, $5, $6, $7, $8, $9)",
    [
      item_id,
      store_id,
      original_price,
      promo_price,
      promo_qty,
      creator_id,
      promo_start,
      promo_end,
      has_promo
    ]
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      message: "Price added"
    });
  });
}

function uploaditemimg(req, res) {
  item_id = req.body.item_id;
  fs.readFile(req.files.image.path, function(err, data) {
    var dirname =
      "C:/GitHub/WeSave/Project Folders/node-postgres-promises/public";
    var newPath = dirname + "/uploads/item_images/item_" + item_id + ".jpg";
    fs.writeFile(newPath, data, function(err) {
      if (err) {
        res.json({
          response: err
        });
      } else {
        dbpath = "item_" + item_id + ".jpg";
        db.none("update items set image=$1 where id=$2", [dbpath, item_id])
          .then(function() {
            res.status(200).json({
              status: "success",
              message: "Updated image path"
            });
          })
          .catch(function(err) {
            return next(err);
          });
      }
    });
  });
}

function getitemimage(req, res) {
  item_id = req.body.item_id;
  var dirname = "/WeSaveRestAPI/node-postgres-promises/public";
  var img = fs.readFileSync(
    dirname + "/uploads/item_images/item_" + item_id + ".jpg"
  );
  res.status(200).json({
    status: "success",
    data: img,
    message: "Image Found"
  });
}

//old get item, not used
function getitem2(req, res) {
  id = req.body.item_id;
  db.one("select * from items where id=$1", id).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Item Found"
    });
  });
}

//Zhi En: not used, merged with get item
function view(req, res) {
  var item_id = req.body.item_id;
  var user_id = req.body.user_id;
  db.none("insert into views(user_id,item_id) values($1, $2)", [
    user_id,
    item_id
  ])
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "View inserted"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: "Already exists"
      });
    });
}

//optimized query by Zhi En
function getitem(req, res) {
  user_id = req.body.user_id;
  item_id = req.body.item_id;
  //add view
  db.none("insert into views(user_id,item_id) values($1, $2)", [
    user_id,
    item_id
  ]).catch(err => {
    //viewer already in db, do nothing
  });

  db.one(
    'select id,name,info,"createdAt",image,creator_id, liker_id, like_count, follower_id, follower_count, view_count\n' +
      "from (select * from items where items.id = $1) as items\n" +
      "left join (select item_id, user_id as liker_id from likes) as l on items.id = l.item_id and l.liker_id = $2\n" +
      "left join (select  item_id,count(user_id) as like_count from likes where item_id = $1 group by item_id) as l2 on items.id = l2.item_id\n" +
      "left join (select item_id, user_id as follower_id from follows) as f on items.id = f.item_id and f.follower_id = $2\n" +
      "left join (select  item_id,count(user_id) as follower_count from follows where item_id = $1 group by item_id) as f2 on items.id = f2.item_id\n" +
      "left join(select item_id, count(*) as view_count from views where item_id = $1 group by item_id) as v on items.id = v.item_id",
    [item_id, user_id]
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Item Found"
    });
  });
}

function getallitems(req, res) {
  db.manyOrNone("select * from items").then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Items Found"
    });
  });
}

function getfollowingitems(req, res) {
  user_id = req.body.user_id;
  db.manyOrNone(
    "select * from items INNER JOIN follows ON items.id=follows.item_id WHERE follows.user_id=$1",
    user_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Items Found"
    });
  });
}

function removefollowingitem(req, res) {
  item_id = req.body.item_id;
  user_id = req.body.user_id;
  db.none("delete from follows WHERE item_id=$1 and user_id = $2", [
    item_id,
    user_id
  ]).then(function() {
    res.status(200).json({
      status: "success",
      message: "Item Deleted"
    });
  });
}

function removelikeitem(req, res) {
  item_id = req.body.item_id;
  user_id = req.body.user_id;
  db.none("delete from likes WHERE item_id=$1 and user_id = $2", [
    item_id,
    user_id
  ]).then(function() {
    res.status(200).json({
      status: "success",
      message: "Item Unliked"
    });
  });
}

function getcontributedpricings(req, res) {
  var user_id = req.body.user_id;
  db.manyOrNone(
    "select pricings.id, stores.store_name, items.name, items.image, items.id item_id, info, original_price, promo_price, promo_qty, promo_start, promo_end, username, createdat, has_promo, lat, lng " +
      "from pricings INNER JOIN users ON pricings.creator_id=users.id INNER JOIN stores ON pricings.store_id=stores.id inner join items on pricings.item_id = items.id " +
      "where users.id = $1 order by createdat desc",
    [user_id]
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Pricing found"
    });
  });
}

function getcontributeditems(req, res) {
  user_id = req.body.user_id;
  db.manyOrNone(
    "select i.*, count(distinct l.user_id) likeCount, count(distinct f.user_id) followCount from items i " +
      " full outer join likes l on l.item_id = i.id full outer join follows f on f.item_id = i.id " +
      ' WHERE creator_id = $1 group by i.id, i.name, i.info, i."createdAt", i."updatedAt", i.image, ' +
      ' i.creator_id, i.creator_id, i.third_cat_id order by "createdAt" desc',
    user_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Items Found"
    });
  });
}

function findbarcode(req, res) {
  barcode = req.body.barcode;
  db.oneOrNone("select item_id from barcodes where barcode=$1", barcode).then(
    function(data) {
      res.status(200).json({
        status: "success",
        data: data,
        message: "Barcode Found"
      });
    }
  );
}

function addbarcode(req, res) {
  var barcode = req.body.barcode;
  var item_id = req.body.item_id;
  db.none("insert into barcodes values($1, $2)", [barcode, item_id]).then(
    function() {
      res.status(200).json({
        status: "success",
        message: "Inserted one barcode"
      });
    }
  );
}

function getpricing(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select pricings.id, store_name, stores.image, original_price, promo_price, promo_qty, promo_start, promo_end, username, createdat, has_promo, lat, lng from pricings " +
      "INNER JOIN users ON pricings.creator_id=users.id INNER JOIN stores ON pricings.store_id=stores.id where item_id = $1",
    [item_id]
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Pricing found"
    });
  });
}

function sortByPricing(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select p.id, store_name, stores.image, original_price, promo_price, promo_qty, promo_start, promo_end, username, createdat, has_promo, lat, lng " +
      "from ( select *,cast(original_price as double precision) price from pricings where item_id = $1 and has_promo = '0' " +
      "union  select *,cast(promo_price as double precision)/cast(promo_qty as double precision) price " +
      "from pricings where item_id = $1 and has_promo = '1' and promo_end >= current_date) as P " +
      "INNER JOIN users ON p.creator_id = users.id INNER JOIN stores ON p.store_id = stores.id order by price limit 10",
    item_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Pricing found"
    });
  });
}

function sortByPricingExpired(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select p.id, store_name, stores.image, original_price, promo_price, promo_qty, promo_start, promo_end, username, createdat, has_promo, lat, lng " +
      "from ( select *,cast(promo_price as double precision)/cast(promo_qty as double precision) price " +
      "from pricings where item_id = $1 and has_promo = '1' and promo_end < current_date) as P " +
      "INNER JOIN users ON p.creator_id = users.id INNER JOIN stores ON p.store_id = stores.id order by price limit 5",
    item_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Pricing found"
    });
  });
}

function sortByDistance(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select p.id, address, store_name, stores.image, original_price, promo_price, promo_qty, promo_start, promo_end, username, createdat, has_promo, lat, lng " +
      "from pricings P INNER JOIN users ON p.creator_id = users.id INNER JOIN stores ON p.store_id = stores.id " +
      "where item_id = $1 ",
    item_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Pricing found"
    });
  });
}

function sortByVotes(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select p.id, store_name, s.image, original_price, promo_price, promo_qty, promo_start, promo_end, username, createdat, has_promo, lat, lng " +
      "from ((select p.id, coalesce(sum(feedback),0) votes from pricings p left join pricing_feedback pf on pf.price_id = p.id " +
      "where item_id = $1 and ((has_promo = '0') or (has_promo = '1' and promo_end >= current_date)) group by p.id) v " +
      "inner join pricings p on v.id = p.id INNER JOIN users u ON p.creator_id = u.id " +
      "INNER JOIN stores s ON p.store_id = s.id ) order by v.votes desc limit 10",
    item_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Pricing found"
    });
  });
}

function sortByVotesExpired(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select p.id, store_name, s.image, original_price, promo_price, promo_qty, promo_start, promo_end, username, createdat, has_promo, lat, lng " +
      "from ((select p.id, coalesce(sum(feedback),0) votes from pricings p left join pricing_feedback pf on pf.price_id = p.id " +
      "where item_id = $1 and has_promo = '1' and promo_end < current_date group by p.id) v " +
      "inner join pricings p on v.id = p.id INNER JOIN users u ON p.creator_id = u.id " +
      "INNER JOIN stores s ON p.store_id = s.id ) order by v.votes desc limit 5",
    item_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Pricing found"
    });
  });
}

function sortByDate(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select p.id, address, store_name, stores.image, original_price, promo_price, promo_qty, promo_start, promo_end, username, createdat, has_promo, lat, lng " +
      "from pricings P INNER JOIN users ON p.creator_id = users.id INNER JOIN stores ON p.store_id = stores.id " +
      "where item_id = $1 and ((has_promo = '1' and promo_end >= current_date) or (has_promo = '0')) order by p.createdat desc limit 10",
    item_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Pricing found"
    });
  });
}

function sortByDateExpired(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select p.id, address, store_name, stores.image, original_price, promo_price, promo_qty, promo_start, promo_end, username, createdat, has_promo, lat, lng " +
      "from pricings P INNER JOIN users ON p.creator_id = users.id INNER JOIN stores ON p.store_id = stores.id " +
      "where item_id = $1 and has_promo = '1' and promo_end < current_date order by p.createdat desc limit 5",
    item_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Pricing found"
    });
  });
}

function addstore(req, res) {
  var store_name = req.body.store_name;
  var category = req.body.category;
  var store_id = req.body.store_id;
  var icon = req.body.icon;
  var address = req.body.address;
  var lat = req.body.lat;
  var long = req.body.long;

  db.none(
    "insert into stores(store_name, type, id, image, address, lat, lng) values($1, $2, $3, $4, $5, $6, $7)",
    [store_name, category, store_id, icon, address, lat, long]
  )
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "Store inserted"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: "Already exists"
      });
    });
}

function getstore(req, res) {
  id = req.body.store_id;
  db.one("select * from stores where id=$1", id).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Store Found"
    });
  });
}

function getallstores(req, res) {
  var type = req.body.type;
  db.manyOrNone("select * from stores where type = $1", type).then(function(
    data
  ) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Stores found"
    });
  });
}

function like(req, res) {
  var item_id = req.body.item_id;
  var user_id = req.body.user_id;
  db.none("insert into likes(user_id,item_id) values($1, $2)", [
    user_id,
    item_id
  ])
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "Like inserted"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: "Already exists"
      });
    });
}

function follow(req, res) {
  var item_id = req.body.item_id;
  var user_id = req.body.user_id;
  db.none("insert into follows(user_id,item_id) values($1, $2)", [
    user_id,
    item_id
  ])
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "Follow inserted"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: "Already exists"
      });
    });
}

//Zhi En: not used, merged with get item
function getlikecount(req, res) {
  var item_id = req.body.item_id;
  db.one("select count(*) from likes where item_id=$1", item_id).then(function(
    data
  ) {
    res.status(200).json({
      status: "success",
      data: data.count,
      message: "success"
    });
  });
}

//Zhi En: not used, merged with get item
function getviewcount(req, res) {
  var item_id = req.body.item_id;
  db.one("select count(*) from views where item_id=$1", item_id).then(function(
    data
  ) {
    res.status(200).json({
      status: "success",
      data: data.count,
      message: "success"
    });
  });
}

function getfollowcount(req, res) {
  var item_id = req.body.item_id;
  db.one("select count(*) from follows where item_id=$1", item_id).then(
    function(data) {
      res.status(200).json({
        status: "success",
        data: data.count,
        message: "success"
      });
    }
  );
}

function getfeedback(req, res) {
  var price_id = req.body.price_id;
  var user_id = req.body.user_id;
  db.oneOrNone(
    "select feedback from pricing_feedback where user_id = $1 and price_id = $2 ",
    [user_id, price_id]
  )
    .then(function(data) {
      res.status(200).json({
        status: "success",
        data: data,
        message: "Feedback Found"
      });
    })
    .catch(function(err) {
      return "Error";
      //return (err);
    });
}

function pricingfeedback(req, res) {
  var price_id = req.body.price_id;
  var user_id = req.body.user_id;
  var feedback = req.body.feedback;
  db.none(
    "insert into pricing_feedback(user_id,price_id,feedback) values($1, $2, $3)",
    [user_id, price_id, feedback]
  )
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "Feedback inserted"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: "Already exists"
      });
    });
}

function updatefeedback(req, res) {
  var price_id = req.body.price_id;
  var user_id = req.body.user_id;
  var feedback = req.body.feedback;
  db.none(
    "update pricing_feedback set feedback = $1 where price_id = $2 and user_id = $3",
    [feedback, price_id, user_id]
  )
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "Updated feedback"
      });
    })
    .catch(function(error) {
      console.log("ERROR:", error.message || error);
    });
}

function getminprice(req, res) {
  var item_id = req.body.item_id;
  var store_ids = req.body.store_ids;
  var query =
    "select price, store_name from pricings INNER JOIN stores " +
    "ON pricings.store_id=stores.id where item_id=" +
    item_id +
    " and price = (select min(price) from pricings where item_id=" +
    item_id +
    " and store_id in (" +
    store_ids +
    "))";
  console.log(query);
  db.manyOrNone(query)
    .then(function(data) {
      res.status(200).json({
        status: "success",
        data: data
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: err
      });
    });
}

function saveplan(req, res) {
  var user_id = req.body.user_id;
  var total = req.body.total;
  var plan = req.body.plan;
  var plan_name = req.body.plan_name;
  db.none(
    "insert into shopping_plans(user_id,plan,total,plan_name) values($1, $2, $3, $4)",
    [user_id, plan, total, plan_name]
  )
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "Shopping plan saved"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed"
      });
    });
}

function removeplan(req, res) {
  var plan_id = req.body.plan_id;
  db.none("delete from shopping_plans WHERE id=$1", plan_id).then(function() {
    res.status(200).json({
      status: "success",
      message: "Plan Deleted"
    });
  });
}

function getshoppingplans(req, res) {
  user_id = req.body.user_id;
  db.manyOrNone("select * from shopping_plans WHERE user_id=$1", user_id).then(
    function(data) {
      res.status(200).json({
        status: "success",
        data: data,
        message: "Plans Retrieved"
      });
    }
  );
}

function searchitems(req, res) {
  query = req.params.query;
  db.manyOrNone(
    "select * from items WHERE LOWER( name ) like $1 ",
    "%" + query.toLowerCase() + "%"
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Items Found"
    }).catch;
  });
}

function searchPopularityitems(req, res) {
  query = req.params.query;
  if (query == "-1") {
    db.manyOrNone(
      "select * from items where name IN(select b.name from follows as a, items as b group by b.name order by count(*) desc LIMIT 5)"
    ).then(function(data) {
      res.status(200).json({
        status: "success",
        data: data,
        message: "Items Found"
      });
    });
  } else {
    db.manyOrNone(
      "select * from items where name IN(select b.name from follows as a, items as b where a.item_id = b.id AND b.name like $1 group by b.name order by count(*) desc)",
      "%" + query.toLowerCase() + "%"
    ).then(function(data) {
      res.status(200).json({
        status: "success",
        data: data,
        message: "Items Found"
      });
    });
  }
}

function searchNearbyStores(req, res) {
  var lat = req.body.lat;
  var lng = req.body.lng;
  var distOption = req.body.distOption;
  var distance;
  switch (distOption) {
    //less or equal 1km
    case "0":
      distance = "1";
      break;
    //less or equal 2
    case "1":
      distance = "2";
      break;
    //dist <= 3
    case "2":
      distance = "3";
      break;
    default:
      distance = "2";
      break;
  }

  db.manyOrNone(
    "select * from(select store_name,address,lat,lng,((6371 * asin(sqrt((sin(radians((lat - $1) / 2))) ^ 2 " +
      "+ cos(radians(lat)) * cos(radians($1)) * (sin(radians(($2 - lng) / 2))) ^ 2))*100)::integer)::float/100 as distance from stores) " +
      "as t where distance <= $3 order by distance;",
    [lat, lng, distance]
  )
    .then(function(data) {
      res.status(200).json({
        status: "success",
        data: data,
        message: "Nearby Supermarket Found"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: "No Supermarket Found"
      });
    });
}

function searchNearestitems(req, res) {
  var lat = req.body.lat;
  var lng = req.body.lng;
  var item = req.body.item;
  var distance = req.body.distance;
  var distanceQuery = null;

  switch (distance) {
    case "Within 1KM":
      console.log("within1km");
      db.manyOrNone(
        "select * from (select c.id,c.name,c.info,c.image,a.original_price,a.promo_price,b.lat,b.lng,(6371 * acos( cos( radians($1) ) * cos( radians( lat) ) " +
          "* cos( radians( lng ) - radians($2) ) + sin( radians($3)) * sin ( radians( lat ) ) )  ) as distance" +
          " from pricings as a join stores as b ON a.store_id = b.id join items as c on a.item_id=c.id where c.name like $4 order by distance) as nearest where distance <=1",
        [lat, lng, lat, "%" + item + "%"]
      ).then(function(data) {
        res.status(200).json({
          status: "success",
          data: data,
          message: "Items Found"
        });
      });
      break;

    case "1KM - 2KM":
      console.log("1km-2km");
      db.manyOrNone(
        "select * from (select c.id,c.name,c.info,c.image,a.original_price,a.promo_price,b.lat,b.lng,(6371 * acos( cos( radians($1) ) * cos( radians( lat) ) " +
          "* cos( radians( lng ) - radians($2) ) + sin( radians($3)) * sin ( radians( lat ) ) )  ) as distance" +
          " from pricings as a join stores as b ON a.store_id = b.id join items as c on a.item_id=c.id where c.name like $4 order by distance) as nearest where distance BETWEEN 1 and 2",
        [lat, lng, lat, "%" + item + "%"]
      ).then(function(data) {
        res.status(200).json({
          status: "success",
          data: data,
          message: "Items Found"
        });
      });
      break;

    case "Beyond 2KM":
      console.log("beyond2km");
      db.manyOrNone(
        "select * from (select c.id,c.name,c.info,c.image,a.original_price,a.promo_price,b.lat,b.lng,(6371 * acos( cos( radians($1) ) * cos( radians( lat) ) " +
          "* cos( radians( lng ) - radians($2) ) + sin( radians($3)) * sin ( radians( lat ) ) )  ) as distance" +
          " from pricings as a join stores as b ON a.store_id = b.id join items as c on a.item_id=c.id where c.name like $4 order by distance) as nearest where distance > 2",
        [lat, lng, lat, "%" + item + "%"]
      ).then(function(data) {
        res.status(200).json({
          status: "success",
          data: data,
          message: "Items Found"
        });
      });
      break;
  }
}

function recommendeditems(req, res) {
  db.manyOrNone(
    "select * from items as a join (select distinct(item_id), Case When  min(original_price) > min(promo_price) Then min(promo_price) When min(promo_price) > min(original_price)  Then min(original_price) End As lowestprice from pricings group by item_id) as b on a.id=b.item_id and lowestprice != '0' join pricings as c on c. item_id = b.item_id and (c.original_price = lowestprice OR c.promo_price = lowestprice) LIMIT 5"
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Items Found"
    });
  });
}

function nearestitems(req, res) {
  var lat = req.body.lat;
  var lng = req.body.lng;

  db.manyOrNone(
    "select * from (select c.id as item_id,c.name,c.info,c.image,b.store_name,a.has_promo,a.original_price,a.promo_price,b.lat,b.lng,(6371 * acos( cos( radians($1) ) * cos( radians( lat) ) " +
      "* cos( radians( lng ) - radians($2) ) + sin( radians($3)) * sin ( radians( lat ) ) )  ) as distance" +
      " from pricings as a join stores as b ON a.store_id = b.id join items as c on a.item_id=c.id order by distance) as nearest LIMIT 5",
    [lat, lng, lat]
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data,
      message: "Items Found"
    });
  });
}

function getfollowers(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select u.id, username from follows f inner join users u on f.user_id = u.id " +
      " WHERE item_id=$1",
    item_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data
    });
  });
}

function getlikers(req, res) {
  var item_id = req.body.item_id;
  db.manyOrNone(
    "select u.id, username from likes l inner join users u on l.user_id = u.id " +
      " WHERE item_id=$1",
    item_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data
    });
  });
}

//Zhi En: not used, merged with get item
function checkfollow(req, res) {
  var item_id = req.body.item_id;
  var user_id = req.body.user_id;
  db.oneOrNone("select * from follows WHERE item_id=$1 and user_id=$2", [
    item_id,
    user_id
  ]).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data
    });
  });
}

//Zhi En: not used, merged with get item
function checklike(req, res) {
  var item_id = req.body.item_id;
  var user_id = req.body.user_id;
  db.oneOrNone("select * from likes WHERE item_id=$1 and user_id=$2", [
    item_id,
    user_id
  ]).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data
    });
  });
}

function storenotification(req, res) {
  var type = req.body.type;
  var user_id = req.body.user_id;
  var item_id = req.body.item_id;
  var recipient_id = req.body.recipient_id;

  db.none(
    "insert into notification_history(type, user_id, item_id, recipient_id) values($1, $2, $3, $4)",
    [type, user_id, item_id, recipient_id]
  )
    .then(function() {
      res.status(200).json({
        status: "success",
        message: "Notification inserted"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: "Already exists"
      });
    });
}

function getnotifications(req, res) {
  var recipient_id = req.body.recipient_id;
  db.manyOrNone(
    "select n.*, i.image, u.username from notification_history n inner join items i on n.item_id = i.id " +
      " inner join users u on u.id = n.user_id " +
      " WHERE recipient_id = $1 order by timestamp desc",
    recipient_id
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data
    });
  });
}

function getcategories(req, res) {
  db.manyOrNone(
    "select 1 as level,* from categories order by category_name"
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data
    });
  });
}

function getsubcategories(req, res) {
  var cat_id = req.body.cat_id;
  db.manyOrNone(
    "select 2 as level,* from subcategories where parent_cat_id = $1 order by category_name",
    [cat_id]
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data
    });
  });
}

function getthirdlvlcategories(req, res) {
  var subcat_id = req.body.subcat_id;
  db.manyOrNone(
    "select 3 as level,* from third_lvl_categories where parent_cat_id = $1 order by category_name",
    [subcat_id]
  ).then(function(data) {
    res.status(200).json({
      status: "success",
      data: data
    });
  });
}

function getitemsbycategory(req, res) {
  cat_id = req.body.cat_id;
  db.manyOrNone(
    "select i.* from items i inner join third_lvl_categories t on i.third_cat_id = t.id " +
      'where t.id = $1 order by i."updatedAt" desc',
    cat_id
  )
    .then(function(data) {
      res.status(200).json({
        status: "success",
        data: data,
        message: "Items Found"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: err
      });
    });
}

function getcategoryname(req, res) {
  third_cat_id = req.body.third_cat_id;
  db.oneOrNone(
    "select c.id as cat_id, c.category_name as category, s.id as subcat_id, s.category_name as subcategory, " +
      "t.id as third_cat_id, t.category_name as third_category from categories c inner join subcategories s on c.id = s.parent_cat_id " +
      "inner join third_lvl_categories t on s.id = t.parent_cat_id " +
      "where t.id = $1",
    third_cat_id
  )
    .then(function(data) {
      res.status(200).json({
        status: "success",
        data: data,
        message: "Category Found"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: err
      });
    });
}

function getuserinfo(req, res) {
  user_id = req.body.user_id;
  db.oneOrNone("select * from users where id = $1 ", user_id)
    .then(function(data) {
      res.status(200).json({
        status: "success",
        data: data,
        message: "User Found"
      });
    })
    .catch(function(err) {
      res.status(200).json({
        status: "failed",
        message: err
      });
    });
}
