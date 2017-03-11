<div id="table-of-contents">
<h2>Table of Contents</h2>
<div id="text-table-of-contents">
<ul>
<li><a href="#sec-1">1. Overview</a></li>
<li><a href="#sec-2">2. Datomic (Backend)</a>
<ul>
<li><a href="#sec-2-1">2.1. Data Structure</a></li>
</ul>
</li>
<li><a href="#sec-3">3. Front End (om-next)</a></li>
<li><a href="#sec-4">4. Database Structure</a></li>
</ul>
</div>
</div>

# Overview<a id="sec-1" name="sec-1"></a>

This tutorial will try to explain how to make the simplest
client/server app using om-next and datomic respectively.

Very big parts of this exercise are the shape of the data and querying
the data.  Lets look at how we might structure some data on the
backend (datomic) first.

# Datomic (Backend)<a id="sec-2" name="sec-2"></a>

## Data Structure<a id="sec-2-1" name="sec-2-1"></a>

Imagine we have datomic data that looks like:

    {:db/id 1
     :car/make "Toyota"
     :car/model "Tacoma"
     :year 2013}
    
    {:db/id 2
     :car/make "BMW"
     :car/model "325xi"
     :year 2001}
    
    {:db/id 3 
     :user/email "fenton.travers@gmail.com"
     :user/age 43
     :user/cars [{:db/id 1}
                 {:db/id 2}]}

We can see the `:user/cars` field points to an array of cars.

In datomic we can use **pull** syntax to indicate which data we want.  A
valid pull *query* would look like:

    [:user/email
     :user/age
     {:user/cars
      [:db/id :car/make :car/model :year]}]

Datomic will happily *hydrate* this *query* to become:

    #:user{:email "fenton.travers@gmail.com",
           :age 43,
           :cars
           [{:db/id 17592186045419,
             :car/make "Toyota",
             :car/model "Tacomaaaa",
             :year 2013}
            {:db/id 17592186045420,
             :car/make "BMW",
             :car/model "325xi",
             :year 2001}]}

Okay, I cheated a little on the original `:db/id`'s, in reality
inserting test data `:db/id`'s looks more like: `:db/id
#db/id[:db.part/user -1]`, but I want to keep this tutorial focused on
conceptual simplicity over absolute correctness.  I'll try to point
out where that occurs.

# Front End (om-next)<a id="sec-3" name="sec-3"></a>

Now we need to structure the front end so we can easily get this info
from the backend.

So in om-next we'd normally construct this as two components.  One is
the parent component which would display the email and age of the
user, then we'd have a table that shows the cars they own.  The two
rows of the table are another component that is reused for each row.

# Database Structure<a id="sec-4" name="sec-4"></a>

A big part of om-next is that it creates norms about how to store you
application data.  Often we have pieces of data that appear in
multiple places in our UI.  Naturally, if it is the same data in
multiple places, we'd like to only have one real copy of it.  Any
other 'copies' are actually references.

To store data in om-next you follow this structure:

    (def app-state { :keyword { id real-information }})

or a real example:

    (def app-state {:curr-user { "fenton.travers@gmail.com" {:age 21 :height 183}}})

We can now add a second location that 'refers' to the first like so:

    (def app-state {:app-owner [:curr-user "fenton.travers@gmail.com"]
                    :curr-user { "fenton.travers@gmail.com" {:age 21 :height 183}}})

The format: `[:keyword id]` is called an `ident`.  Its a reference to
some shared data.
