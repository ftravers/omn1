(ns omn1.webpage
  (:require
   #?@(:cljs
       [[om.next :as om :refer-macros [defui]]
        [om.dom :as dom :refer [div ul li table tr td th thead tbody]]
        [goog.dom :as gdom]
        [omn1.data :as dat]]
       :clj
       [[om.next :as om :refer [defui]]
        [om.dom :as dom :refer [div ul li table tr td th]]
        [omn1.data :as dat]])))

(defui Car
  static om/Ident
  (ident [this {:keys [id]}]
         [:car/by-id id])
  static om/IQuery
  (query [this] [:id :car/make :car/model :year])
  Object
  (render
   [this]
   (let [{id :id make :car/make model :car/model year :year} (om/props this)]
     (tr nil
         (td nil make)
         (td nil model)
         (td nil year)
         (td nil id)))))

(def car (om/factory Car {:keyfn :id}))

(defui MyCars
  static om/IQuery
  (query [this] [:current/user {:user/cars (om/get-query Car)}])
  Object
  (render
   [this]
   (let [{user :current/user cars :user/cars} (om/props this)]
     (div nil
          (div nil (str "Current User: " (:user/email user)))
          (div nil (str "Age: " (:user/age user)))
          (div nil "User Cars:")
          (table
           nil
           (thead
            nil
            (tr nil
                (th nil "Make")
                (th nil "Model")
                (th nil "Year")
                (th nil "ID")))
           (tbody nil (map car cars)))))))

#?(:cljs
   (om/add-root! dat/reconciler MyCars (gdom/getElement "app")))

;; ------------ test functions

(def simple-factory (om/factory MyCars))
(dom/render-to-str (simple-factory))
(om/component? MyCars)
(om/get-query MyCars)
