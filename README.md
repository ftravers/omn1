<div id="table-of-contents">
<h2>Table of Contents</h2>
<div id="text-table-of-contents">
<ul>
<li><a href="#sec-1">1. Overview</a></li>
<li><a href="#sec-2">2. Datomic - the backend</a>
<ul>
<li><a href="#sec-2-1">2.1. Data Structure</a></li>
</ul>
</li>
<li><a href="#sec-3">3. Front End (om-next)</a></li>
<li><a href="#sec-4">4. Om-Next Remotes</a></li>
<li><a href="#sec-5">5. Database Structure</a></li>
</ul>
</div>
</div>

# Overview<a id="sec-1" name="sec-1"></a>

This tutorial will try to explain how to make the simplest
client/server app using om-next and datomic respectively.

Very big parts of this exercise are the shape of the data and querying
the data.  Lets look at how we might structure some data on the
backend (datomic) first.

# Datomic - the backend<a id="sec-2" name="sec-2"></a>

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

We can see the `:user/cars` field points to an array of cars that
fenton owns.

**NOTE:** I'm simplifying some aspects of this tutorial because I want
to keep focused on conceptual simplicity over absolute syntactical
correctness.

In datomic we can use **pull** syntax to indicate which data we want.  A
valid pull *query* would look like:

    [:user/email
     :user/age
     {:user/cars
      [:db/id :car/make :car/model :year]}]

which datomic will happily *hydrate*, or fill out the fields of this
*query* to become:

    #:user{:email "fenton.travers@gmail.com",
           :age 43,
           :cars
           [{:db/id 1,
             :car/make "Toyota",
             :car/model "Tacoma",
             :year 2013}
            {:db/id 2,
             :car/make "BMW",
             :car/model "325xi",
             :year 2001}]}

So we have (kind of) demonstrated how we can extract data from
datomic, lets see if we can get this to jive with the om-next front
end now.

# Front End (om-next)<a id="sec-3" name="sec-3"></a>

Now we need to structure the front end so we can easily get this info
from the backend.

So in om-next we'd normally construct this as two components.  One is
the parent component which would display the email and age of the
user, then we'd have a table that shows the cars they own.  The two
rows of the table are another component that is reused for each row.

Om-next components declare the data they require, in our example we'd
have something like:

    (defui Car
      (query [this] [:db/id :car/make :car/model :year]))
    
    (defui MyCars
      (query [this] [:user/email :user/age {:user/cars (om/get-query Car)}]))

Now om-next will create one combined query which we can pass along to
datomic to *hydrate*.

So the final full query looks like:

    [:user/email
     :user/age
     {:user/cars
      [:db/id :car/make :car/model :year]}]

Which is **identical** to the query we were able to pass to datomic, so
on the surface this looks great.  Next we'll look at how om-next
queries a backend and how it merges the results into the local data
structure.

# Om-Next Remotes<a id="sec-4" name="sec-4"></a>

Om-next remotes are handled in a function definition.  This function
can be passed anything that the reader has.  A good place to look at
what it is sent is this URL: [Alan Kay om-next tutorial - State Reads](https://awkay.github.io/om-tutorial/#!/om_tutorial.E_State_Reads_and_Parsing).

The last parameter your remote handling function will recieve is a
callback function that you should call with the results of your actual
server side response.  Critical here is the shape of your local state
store, and the data that you are passing into the callback.

Lets look at the aspect of returning data from a remote function and
seeing how/what is able to be merged into your local state store.

# Database Structure<a id="sec-5" name="sec-5"></a>

A big part of om-next is that it creates norms about how to store your
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
