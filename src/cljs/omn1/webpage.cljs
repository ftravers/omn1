(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div ul li table tr td th thead tbody]]
   [goog.dom :as gdom]
   [omn1.data :as dat]
   [taoensso.timbre :refer-macros [info]]))

(taoensso.timbre/merge-config! {:level :error :ns-blacklist ["omn1.*"]} )

;; This method takes the props the component will receive (or has
;; received) and return a unique identifier.

;; Car will receive:
;; {id {:car/make "Subaru" :car/model "Forester" :year 2001}}

(defui Car
  static om/Ident
  (ident [this props]
         (let [id (first (keys props))]
           [:car/by-id id]))
  static om/IQuery
  (query [this] [:db/id :car/make :car/model :year])
  Object
  (render
   [this]
   (let [{id :db/id make :car/make model :car/model year :year} (om/props this)]
     (tr nil
         (td nil make)
         (td nil model)
         (td nil year)
         (td nil id)))))

(defn keyfn [props]
  (first (keys props)))

(def car (om/factory Car {:keyfn keyfn}))

(defui UserCars
  static om/IQuery
  (query [this] [:curr-user {:user/cars (om/get-query Car)}])
  Object
  (render
   [this]
   (.log js/console (str "UserCars props: " (om/props this)))
   (let [props (om/props this)
         {cars :user/cars curr-user :curr-user} props
         email (first (keys curr-user))
         cu-data (get curr-user email)
         {age :age height :height} cu-data]
     (.log js/console (str "Email: " email))
     (.log js/console (str "Curr User Data: " cu-data))
     (.log js/console (str "Cars: " cars))
     (div nil
          (div nil (str "Current User: " email))
          (div nil (str "Age: " age))
          (div nil (str "Height: " height))
          (div nil "User Cars:")
          (table nil
                 (thead nil
                        (tr nil
                            (th nil "Make")
                            (th nil "Model")
                            (th nil "Year")
                            (th nil "ID")))
                 (tbody nil (map car cars)))))))

(om/add-root! dat/reconciler UserCars (gdom/getElement "app"))

;; ------------ test functions

;; #?(:clj (do (def simple-factory (om/factory UserCars))
;;             (dom/render-to-str (simple-factory))
;;             (om/component? UserCars)
;;             (om/get-query UserCars)))
