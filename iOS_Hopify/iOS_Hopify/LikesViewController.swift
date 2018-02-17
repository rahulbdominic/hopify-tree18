import UIKit
import RxSwift
import RxCocoa

class LikesViewController: UIViewController, UITableViewDelegate {

    @IBOutlet weak var likeTableView: UITableView!


    let disposeBag = DisposeBag()
    var selectedLikes: [String] = []

    let placeTitles: Observable<[String]> = Observable.just([
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

        // Sets self as tableview delegate
        likeTableView
            .rx.setDelegate(self)
            .disposed(by: disposeBag)

        setupCurrentQuestionObserver()
    }

    private func setupCurrentQuestionObserver() {
        placeTitles
            .bind(to: likeTableView.rx.items) { (tableView, row, element) in
                let cell = tableView.dequeueReusableCell(withIdentifier: "likeCell")!
                cell.textLabel?.text = "\(element)"
                return cell
            }
            .disposed(by: disposeBag)

        likeTableView.rx.itemSelected
            .subscribe(onNext: { [weak self] indexPath in
                let cell = self?.likeTableView.cellForRow(at: indexPath)
                self?.selectedLikes.append((cell?.textLabel?.text)!)
                cell?.backgroundColor = UIColor(displayP3Red: 0, green: 100, blue: 100, alpha: 0.5)
                cell?.isSelected = false
                print(self?.selectedLikes as! [String])
            })
            .disposed(by: disposeBag)
    }


    @IBAction func nextPage(_ sender: Any) {
    }
}
