(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div ul li]]
   [goog.dom :as gdom]
   [omn1.data :as dat]))

(defui Car
  static om/Ident
  (ident [this {:keys [id]}]
         [:car/by-id id])

  static om/IQuery
  (query [this] [:id :make :model :year])
  Object
  (render
   [this]
   (let [{:keys [id make model year]} (om/props this)]
     (li nil
         (str "Make: " make
              ". Model: " model
              ". Year: " year
              ". ID: " id)))))

(def car (om/factory Car {:keyfn :id}))

(defui MyCars
  static om/IQuery
  (query [this] [:current/user {:my-cars (om/get-query Car)}])
  Object
  (render
   [this]
   (let [{user :current/user cars :my-cars} (om/props this)]
     (div nil (div nil (str "Current User: " (:user/name user)))
      (ul nil
          (map car cars) )))))

(om/add-root! dat/reconciler MyCars (gdom/getElement "app"))

(om/component? MyCars)
(om/get-query MyCars)
