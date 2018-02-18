import UIKit
import RxSwift
import RxCocoa
import PhoneNumberKit
import SDCAlertView

class LikesViewController: UIViewController, UITableViewDelegate {

    @IBOutlet weak var likeTableView: UITableView!


    let disposeBag = DisposeBag()
    var selectedLikes: [String] = []

    var places = [
        "accounting",
        "airport",
        "amusement_park",
        "aquarium",
        "art_gallery",
        "bakery",
        "bar",
        "beauty_salon",
        "bicycle_store",
        "book_store",
        "bowling_alley",
        "bus_station",
        "cafe",
        "casino",
        "church",
        "clothing_store",
        "department_store",
        "doctor",
        "florist",
        "furniture_store",
        "gym",
        "hair_care",
        "liquor_store",
        "meal_delivery",
        "meal_takeaway",
        "movie_theater",
        "museum",
        "night_club",
        "park",
        "restaurant",
        "shopping_mall",
        "spa",
        "stadium",
        "subway_station",
        "supermarket",
        "train_station",
        "zoo"]
    let placeTitles: Observable<[String]> = Observable.just( [
        "accounting",
        "airport",
        "amusement_park",
        "aquarium",
        "art_gallery",
        "bakery",
        "bar",
        "beauty_salon",
        "bicycle_store",
        "book_store",
        "bowling_alley",
        "bus_station",
        "cafe",
        "casino",
        "church",
        "clothing_store",
        "department_store",
        "doctor",
        "florist",
        "furniture_store",
        "gym",
        "hair_care",
        "liquor_store",
        "meal_delivery",
        "meal_takeaway",
        "movie_theater",
        "museum",
        "night_club",
        "park",
        "restaurant",
        "shopping_mall",
        "spa",
        "stadium",
        "subway_station",
        "supermarket",
        "train_station",
        "zoo"])

    override func viewDidLoad() {
        super.viewDidLoad()

        likeTableView.frame = CGRect(x: likeTableView.frame.origin.x, y: likeTableView.frame.origin.y, width: view.frame.width, height: likeTableView.frame.height)

        // Sets self as tableview delegate
        likeTableView
            .rx.setDelegate(self)
            .disposed(by: disposeBag)

        setupCurrentQuestionObserver()
    }

    private func setupCurrentQuestionObserver() {
        placeTitles
            .bind(to: likeTableView.rx.items) { (tableView, row, element) in
                let cell = tableView.dequeueReusableCell(withIdentifier: "likeCell") as! ChoiceCell
                cell.titleLabel.text = "\(element)"

                //cell.cellImage?.image = UIImage(named: "Icon-40")

                return cell
            }
            .disposed(by: disposeBag)

        likeTableView.rx.itemSelected
            .subscribe(onNext: { [weak self] indexPath in
                let cell = self?.likeTableView.cellForRow(at: indexPath) as! ChoiceCell
                self?.selectedLikes.append(cell.titleLabel.text!)
                cell.backgroundColor = UIColor(displayP3Red: 0, green: 100.0/255.0, blue: 0, alpha: 1)
                cell.titleLabel.textColor = .white
                cell.isSelected = false
                print(self?.selectedLikes as! [String])
            })
            .disposed(by: disposeBag)
    }


    @IBAction func nextPage(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let controller = storyboard.instantiateViewController(withIdentifier: "SettingsViewController") as? SettingsViewController
        controller?.data.likes = selectedLikes
        self.navigationController?.pushViewController(controller!, animated: true)
    }
}
