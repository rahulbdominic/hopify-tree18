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

final class ChoiceCell: UITableViewCell {
    var titleLabel: UILabel! = UILabel()

    private let redColor   = UIColor(red: 231 / 255, green: 76 / 255, blue: 60 / 255, alpha: 1)
    private let greenColor = UIColor(red: 46 / 255, green: 204 / 255, blue: 113 / 255, alpha: 1)

    var choiceModel: ChoiceModel? {
        didSet {
            layoutCell()
        }
    }

    override var isSelected: Bool {
        didSet {
            layoutCell()
        }
    }

    var displayAnswers: Bool = false {
        didSet {
            layoutCell()
        }
    }

    private func layoutCell() {
        titleLabel.text = choiceModel?.title
    }
}