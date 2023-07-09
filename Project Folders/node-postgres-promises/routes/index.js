var express = require("express");
var router = express.Router();
var db = require("../queries");
var fs = require("fs");

/* GET home page. */
router.get("/", function(req, res, next) {
  res.render("index", { title: "Express" });
});

router.post("/api/register", db.register);
router.post("/api/login", db.login);
router.post("/api/finduser", db.finduser);
router.post("/api/resetpass", db.resetpass);
router.post("/api/createitem", db.createitem);
router.post("/api/addprice", db.addprice);
router.post("/api/getitem", db.getitem);
router.post("/api/getallitems", db.getallitems);
router.post("/api/getfollowingitems", db.getfollowingitems);
router.post("/api/getcontributeditems", db.getcontributeditems);
router.post("/api/getcontributedpricings", db.getcontributedpricings);
router.post("/api/checkfollow", db.checkfollow);
router.post("/api/checklike", db.checklike);
router.post("/api/removefollowingitem", db.removefollowingitem);
router.post("/api/removelikeitem", db.removelikeitem);
router.post("/api/findbarcode", db.findbarcode);
router.post("/api/addbarcode", db.addbarcode);
router.post("/api/getpricing", db.getpricing);
router.post("/api/sortByPricing", db.sortByPricing);
router.post("/api/sortByPricingExpired", db.sortByPricingExpired);
router.post("/api/sortByDistance", db.sortByDistance);
router.post("/api/sortByVotes", db.sortByVotes);
router.post("/api/sortByVotesExpired", db.sortByVotesExpired);
router.post("/api/sortByDate", db.sortByDate);
router.post("/api/sortByDateExpired", db.sortByDateExpired);
router.post("/api/addstore", db.addstore);
router.post("/api/getstore", db.getstore);
router.post("/api/getallstores", db.getallstores);
router.post("/api/like", db.like);
router.post("/api/view", db.view);
router.post("/api/follow", db.follow);
router.post("/api/getlikecount", db.getlikecount);
router.post("/api/getviewcount", db.getviewcount);
router.post("/api/getfeedback", db.getfeedback);
router.post("/api/pricingfeedback", db.pricingfeedback);
router.post("/api/updatefeedback", db.updatefeedback);
router.post("/api/getfeedback", db.getfeedback);
router.post("/api/uploaditemimg", db.uploaditemimg);
router.post("/api/getitemimg", db.getitemimage);
router.post("/api/getminprice", db.getminprice);
router.post("/api/saveplan", db.saveplan);
router.post("/api/removeplan", db.removeplan);
router.post("/api/getshoppingplans", db.getshoppingplans);
router.get("/api/searchitems/:query", db.searchitems);
router.post("/api/getfollowers", db.getfollowers);
router.post("/api/getlikers", db.getlikers);
router.post("/api/getnotifications", db.getnotifications);
router.post("/api/storenotification", db.storenotification);
router.post("/api/getcategories", db.getcategories);
router.post("/api/getsubcategories", db.getsubcategories);
router.post("/api/getthirdlvlcategories", db.getthirdlvlcategories);
router.post("/api/getitemsbycategory", db.getitemsbycategory);
router.post("/api/getcategoryname", db.getcategoryname);
router.post("/api/getuserinfo", db.getuserinfo);
router.get("/api/searchPopularityitems/:query", db.searchPopularityitems);
router.post("/api/searchNearbyStores", db.searchNearbyStores);
router.post("/api/searchNearestitems", db.searchNearestitems);
router.post("/api/recommendeditems", db.recommendeditems);
router.post("/api/nearestitems", db.nearestitems);

//comment
router.post("/api/getcomments", db.getComments);
router.post("/api/insertComment", db.insertComment);

module.exports = router;
