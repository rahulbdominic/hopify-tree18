//
//  CellView.swift
//  Hopify
//
//  Created by Bart Chrzaszcz on 2018-02-17.
//  Copyright © 2018 Bart Chrzaszcz. All rights reserved.
//

import UIKit

struct ChoiceModel {
    let title: String
}

class ChoiceCell: UITableViewCell {
    var title: UILabel! = UILabel()

    @IBOutlet weak var cellImage: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    //@IBOutlet weak var cellImage: UIImageView!
    //@IBOutlet weak var title: UILabel!
}
